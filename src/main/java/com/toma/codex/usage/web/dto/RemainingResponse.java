package com.toma.codex.usage.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RemainingResponse(
	BigDecimal remaining,
	String unit,
	String status,
	String dataSource,
	boolean cached,
	OffsetDateTime lastUpdatedAt,
	String message
) {
}
