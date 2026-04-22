package com.toma.codex.usage.web.dto;

import java.math.BigDecimal;

public record CostSummaryResponse(
	BigDecimal totalCostUsd
) {
}
