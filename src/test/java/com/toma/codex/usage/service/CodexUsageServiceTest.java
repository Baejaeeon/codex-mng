package com.toma.codex.usage.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.toma.codex.usage.client.CodexUsageClient;
import com.toma.codex.usage.common.config.CodexUsageProperties;
import com.toma.codex.usage.model.AvailabilityStatus;
import com.toma.codex.usage.model.DataSourceType;
import com.toma.codex.usage.model.UsageSnapshot;
import com.toma.codex.usage.model.UserScopeMode;
import com.toma.codex.usage.web.UsageResponseMapper;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class CodexUsageServiceTest {

	@Test
	void cachesSnapshotWithinTtl() {
		CodexUsageProperties properties = new CodexUsageProperties();
		properties.setCacheTtl(Duration.ofMinutes(5));
		Clock clock = Clock.fixed(Instant.parse("2026-04-20T00:00:00Z"), ZoneOffset.UTC);
		AtomicInteger calls = new AtomicInteger();

		CodexUsageClient client = () -> {
			calls.incrementAndGet();
			return new UsageSnapshot(
				"PRO",
				BigDecimal.valueOf(10),
				BigDecimal.valueOf(100),
				BigDecimal.valueOf(90),
				"TOKENS",
				AvailabilityStatus.AVAILABLE,
				OffsetDateTime.parse("2026-04-01T00:00:00Z"),
				OffsetDateTime.parse("2026-04-30T23:59:59Z"),
				OffsetDateTime.parse("2026-05-01T00:00:00Z"),
				120L,
				2000L,
				850L,
				BigDecimal.valueOf(12.34),
				OffsetDateTime.parse("2026-04-01T00:00:00Z"),
				OffsetDateTime.parse("2026-04-30T23:59:59Z"),
				OffsetDateTime.parse("2026-04-20T00:00:00Z"),
				DataSourceType.OFFICIAL_API,
				true,
				"ok",
				false
			);
		};

		CodexUsageService service = new CodexUsageService(client, properties, new UsageResponseMapper(), clock, "codex-mng");
		var first = service.getDashboard("user@test.com");
		var second = service.getDashboard("user@test.com");

		assertThat(calls.get()).isEqualTo(1);
		assertThat(first.cached()).isFalse();
		assertThat(second.cached()).isTrue();
		assertThat(second.usageActivity().totalRequests()).isEqualTo(120L);
		assertThat(second.costs().totalCostUsd()).isEqualByComparingTo("12.34");
		assertThat(second.scope()).isEqualTo("ORGANIZATION_SHARED");
	}

	@Test
	void returnsNotMappedStatusWhenEmailProjectModeHasNoMapping() {
		CodexUsageProperties properties = new CodexUsageProperties();
		properties.setUserScopeMode(UserScopeMode.EMAIL_PROJECT);
		Clock clock = Clock.fixed(Instant.parse("2026-04-20T00:00:00Z"), ZoneOffset.UTC);

		CodexUsageClient client = () -> new UsageSnapshot(
			"PRO",
			null,
			null,
			null,
			"TOKENS",
			AvailabilityStatus.AVAILABLE,
			OffsetDateTime.parse("2026-04-01T00:00:00Z"),
			OffsetDateTime.parse("2026-04-30T23:59:59Z"),
			OffsetDateTime.parse("2026-05-01T00:00:00Z"),
			120L,
			2000L,
			850L,
			BigDecimal.valueOf(12.34),
			OffsetDateTime.parse("2026-04-01T00:00:00Z"),
			OffsetDateTime.parse("2026-04-30T23:59:59Z"),
			OffsetDateTime.parse("2026-04-20T00:00:00Z"),
			DataSourceType.OFFICIAL_API,
			true,
			"ok",
			false
		);

		CodexUsageService service = new CodexUsageService(client, properties, new UsageResponseMapper(), clock, "codex-mng");
		var response = service.getDashboard("not-mapped@sample.com");

		assertThat(response.scope()).isEqualTo("EMAIL_PROJECT");
		assertThat(response.mappingStatus()).isEqualTo("NOT_MAPPED");
		assertThat(response.usageActivity().totalRequests()).isNull();
		assertThat(response.message()).contains("not mapped");
	}
}
