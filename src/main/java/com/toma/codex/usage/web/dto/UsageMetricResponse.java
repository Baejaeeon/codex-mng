package com.toma.codex.usage.web.dto;

import java.math.BigDecimal;

public record UsageMetricResponse(
	BigDecimal used,
	BigDecimal limit,
	BigDecimal remaining,
	String unit,
	BigDecimal usageRate,
	String status
) {
}
