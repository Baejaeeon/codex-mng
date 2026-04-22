package com.toma.codex.usage.web.dto;

import java.time.OffsetDateTime;

public record BillingCycleResponse(
	OffsetDateTime startedAt,
	OffsetDateTime endsAt,
	OffsetDateTime nextRefreshAt
) {
}
