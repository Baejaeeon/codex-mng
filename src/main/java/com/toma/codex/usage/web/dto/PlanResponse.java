package com.toma.codex.usage.web.dto;

import java.time.OffsetDateTime;

public record PlanResponse(
	String planName,
	String dataSource,
	String message,
	boolean cached,
	OffsetDateTime lastUpdatedAt
) {
}
