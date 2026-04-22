package com.toma.codex.usage.web.dto;

import java.time.OffsetDateTime;

public record SummaryResponse(
	String applicationName,
	String userEmail,
	String scope,
	String mappingStatus,
	UsageActivityResponse usageActivity,
	CostSummaryResponse costs,
	BillingCycleResponse queryWindow,
	String dataSource,
	boolean cached,
	OffsetDateTime lastUpdatedAt,
	String message
) {
}
