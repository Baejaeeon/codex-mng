package com.toma.codex.usage.common.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CodexUsageCoreConfig {

	@Bean
	public Clock usageClock() {
		return Clock.systemDefaultZone();
	}
}
