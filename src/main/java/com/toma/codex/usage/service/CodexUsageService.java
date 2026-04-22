package com.toma.codex.usage.service;

import com.toma.codex.usage.client.CodexUsageClient;
import com.toma.codex.usage.common.config.CodexUsageProperties;
import com.toma.codex.usage.common.exception.ExternalApiException;
import com.toma.codex.usage.model.AvailabilityStatus;
import com.toma.codex.usage.model.UsageSnapshot;
import com.toma.codex.usage.model.UserScopeMode;
import com.toma.codex.usage.web.UsageResponseMapper;
import com.toma.codex.usage.web.dto.DashboardResponse;
import com.toma.codex.usage.web.dto.PlanResponse;
import com.toma.codex.usage.web.dto.RemainingResponse;
import com.toma.codex.usage.web.dto.SummaryResponse;
import java.util.Locale;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CodexUsageService {

	private static final Logger log = LoggerFactory.getLogger(CodexUsageService.class);

	private final CodexUsageClient usageClient;
	private final CodexUsageProperties properties;
	private final UsageResponseMapper mapper;
	private final Clock clock;
	private final String applicationName;
	private volatile CachedSnapshot cachedSnapshot;

	public CodexUsageService(
		CodexUsageClient usageClient,
		CodexUsageProperties properties,
		UsageResponseMapper mapper,
		Clock usageClock,
		@Value("${spring.application.name}") String applicationName
	) {
		this.usageClient = usageClient;
		this.properties = properties;
		this.mapper = mapper;
		this.clock = usageClock;
		this.applicationName = applicationName;
	}

	public DashboardResponse getDashboard(String userEmail) {
		ScopedSnapshot scoped = getScopedSnapshot(false, userEmail);
		return mapper.toDashboard(applicationName, scoped.snapshot(), scoped.userEmail(), scoped.scope(), scoped.mappingStatus());
	}

	public DashboardResponse refreshDashboard(String userEmail) {
		ScopedSnapshot scoped = getScopedSnapshot(true, userEmail);
		return mapper.toDashboard(applicationName, scoped.snapshot(), scoped.userEmail(), scoped.scope(), scoped.mappingStatus());
	}

	public SummaryResponse getSummary(String userEmail) {
		ScopedSnapshot scoped = getScopedSnapshot(false, userEmail);
		return mapper.toSummary(applicationName, scoped.snapshot(), scoped.userEmail(), scoped.scope(), scoped.mappingStatus());
	}

	public PlanResponse getPlan(String userEmail) {
		return mapper.toPlan(getScopedSnapshot(false, userEmail).snapshot());
	}

	public RemainingResponse getRemaining(String userEmail) {
		return mapper.toRemaining(getScopedSnapshot(false, userEmail).snapshot());
	}

	private ScopedSnapshot getScopedSnapshot(boolean forceRefresh, String userEmail) {
		UsageSnapshot source = getSnapshot(forceRefresh);
		String normalizedEmail = normalizeEmail(userEmail);
		UserScopeMode mode = properties.getUserScopeMode();

		if (mode == UserScopeMode.NOT_SUPPORTED) {
			String message = "User-level usage is not supported for this setup.";
			return new ScopedSnapshot(maskUsage(source, message), normalizedEmail, "NOT_SUPPORTED", "NOT_SUPPORTED");
		}

		if (mode == UserScopeMode.EMAIL_PROJECT) {
			if (normalizedEmail == null || !properties.getUserProjectMapping().containsKey(normalizedEmail)) {
				String message = "User is not mapped to an OpenAI project.";
				return new ScopedSnapshot(maskUsage(source, message), normalizedEmail, "EMAIL_PROJECT", "NOT_MAPPED");
			}
			return new ScopedSnapshot(source, normalizedEmail, "EMAIL_PROJECT", "MAPPED");
		}

		return new ScopedSnapshot(source, normalizedEmail, "ORGANIZATION_SHARED", "ORGANIZATION_SHARED");
	}

	private UsageSnapshot maskUsage(UsageSnapshot source, String message) {
		return new UsageSnapshot(
			source.planName(),
			source.used(),
			source.limit(),
			source.remaining(),
			source.unit(),
			AvailabilityStatus.NOT_SUPPORTED,
			source.startedAt(),
			source.endsAt(),
			source.nextRefreshAt(),
			null,
			null,
			null,
			null,
			source.windowStart(),
			source.windowEnd(),
			source.lastUpdatedAt(),
			source.dataSource(),
			source.refreshAvailable(),
			message,
			source.cached()
		);
	}

	private String normalizeEmail(String email) {
		if (email == null || email.isBlank()) {
			return null;
		}
		return email.trim().toLowerCase(Locale.ROOT);
	}

	private UsageSnapshot getSnapshot(boolean forceRefresh) {
		Instant now = clock.instant();
		CachedSnapshot current = cachedSnapshot;
		if (!forceRefresh && current != null && now.isBefore(current.expiresAt())) {
			return current.snapshot().withCached(true, current.snapshot().message(), current.snapshot().lastUpdatedAt());
		}
		return fetchAndCache(now, forceRefresh);
	}

	private synchronized UsageSnapshot fetchAndCache(Instant now, boolean forceRefresh) {
		CachedSnapshot current = cachedSnapshot;
		if (!forceRefresh && current != null && now.isBefore(current.expiresAt())) {
			return current.snapshot().withCached(true, current.snapshot().message(), current.snapshot().lastUpdatedAt());
		}

		try {
			UsageSnapshot fetched = usageClient.fetchUsage();
			UsageSnapshot normalized = fetched.withCached(false, fetched.message(), OffsetDateTime.now(clock));
			cachedSnapshot = new CachedSnapshot(normalized, now.plus(properties.getCacheTtl()));
			log.info(
				"Fetched codex usage snapshot: provider={}, dataSource={}, cacheTtl={}s",
				properties.getProvider(),
				normalized.dataSource(),
				properties.getCacheTtl().toSeconds()
			);
			return normalized;
		} catch (ExternalApiException ex) {
			log.warn("Failed to fetch codex usage snapshot: code={}, message={}", ex.getCode(), ex.getMessage());
			if (current != null) {
				String fallbackMessage = current.snapshot().message() + " (serving cached snapshot due to external API error)";
				return current.snapshot().withCached(true, fallbackMessage, current.snapshot().lastUpdatedAt());
			}
			throw ex;
		}
	}

	record CachedSnapshot(UsageSnapshot snapshot, Instant expiresAt) {
	}

	record ScopedSnapshot(UsageSnapshot snapshot, String userEmail, String scope, String mappingStatus) {
	}
}
