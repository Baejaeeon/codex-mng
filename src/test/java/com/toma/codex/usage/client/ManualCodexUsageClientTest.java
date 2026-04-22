package com.toma.codex.usage.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.toma.codex.usage.common.config.CodexUsageProperties;
import com.toma.codex.usage.model.AvailabilityStatus;
import com.toma.codex.usage.model.DataSourceType;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ManualCodexUsageClientTest {

	@Test
	void returnsManualRequiredSnapshot() {
		CodexUsageProperties properties = new CodexUsageProperties();
		properties.getManual().setPlanName("MANUAL_PLAN");
		properties.getManual().setUnit("REQUESTS");
		properties.getManual().setMessage("manual mode");
		Clock fixedClock = Clock.fixed(Instant.parse("2026-04-20T00:00:00Z"), ZoneOffset.UTC);

		ManualCodexUsageClient client = new ManualCodexUsageClient(properties, fixedClock);
		var snapshot = client.fetchUsage();

		assertThat(snapshot.planName()).isEqualTo("MANUAL_PLAN");
		assertThat(snapshot.unit()).isEqualTo("REQUESTS");
		assertThat(snapshot.status()).isEqualTo(AvailabilityStatus.MANUAL_REQUIRED);
		assertThat(snapshot.dataSource()).isEqualTo(DataSourceType.MANUAL_REQUIRED);
		assertThat(snapshot.message()).isEqualTo("manual mode");
		assertThat(snapshot.used()).isNull();
		assertThat(snapshot.limit()).isNull();
		assertThat(snapshot.remaining()).isNull();
	}
}
