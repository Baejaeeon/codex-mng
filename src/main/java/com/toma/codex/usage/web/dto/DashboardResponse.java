package com.toma.codex.usage.web.dto;

import java.time.OffsetDateTime;

public record DashboardResponse(
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
	OffsetDateTime nextRefreshAt,
	String message,
	boolean refreshAvailable
) {
}
