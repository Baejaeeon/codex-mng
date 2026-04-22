package com.toma.codex.usage.common.health;

import com.toma.codex.usage.common.config.CodexUsageProperties;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("codexUsage")
public class CodexUsageHealthIndicator implements HealthIndicator {

	private final CodexUsageProperties properties;

	public CodexUsageHealthIndicator(CodexUsageProperties properties) {
		this.properties = properties;
	}

	@Override
	public Health health() {
		return Health.up()
			.withDetail("provider", properties.getProvider())
			.withDetail("baseUrl", properties.getBaseUrl())
			.withDetail("cacheTtlSeconds", properties.getCacheTtl().toSeconds())
			.withDetail("tokenConfigured", StringUtils.hasText(properties.getInternalToken()))
			.build();
	}
}
