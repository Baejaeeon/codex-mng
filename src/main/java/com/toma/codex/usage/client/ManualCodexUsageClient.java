package com.toma.codex.usage.client;

import com.toma.codex.usage.common.config.CodexUsageProperties;
import com.toma.codex.usage.model.AvailabilityStatus;
import com.toma.codex.usage.model.DataSourceType;
import com.toma.codex.usage.model.UsageSnapshot;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "codex.usage", name = "provider", havingValue = "manual", matchIfMissing = true)
public class ManualCodexUsageClient implements CodexUsageClient {

	private final CodexUsageProperties properties;
	private final Clock clock;

	public ManualCodexUsageClient(CodexUsageProperties properties, Clock usageClock) {
		this.properties = properties;
		this.clock = usageClock;
	}

	@Override
	public UsageSnapshot fetchUsage() {
		OffsetDateTime now = OffsetDateTime.now(clock);
		OffsetDateTime nextRefresh = now.plusDays(30);
		return new UsageSnapshot(
			properties.getManual().getPlanName(),
			null,
			null,
			null,
			properties.getManual().getUnit(),
			AvailabilityStatus.MANUAL_REQUIRED,
			null,
			null,
			nextRefresh,
			null,
			null,
			null,
			null,
			null,
			null,
			now,
			DataSourceType.MANUAL_REQUIRED,
			false,
			properties.getManual().getMessage(),
			false
		);
	}
}
