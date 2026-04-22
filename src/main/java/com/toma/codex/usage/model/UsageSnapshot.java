package com.toma.codex.usage.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UsageSnapshot(
	String planName,
	BigDecimal used,
	BigDecimal limit,
	BigDecimal remaining,
	String unit,
	AvailabilityStatus status,
	OffsetDateTime startedAt,
	OffsetDateTime endsAt,
	OffsetDateTime nextRefreshAt,
	Long totalRequests,
	Long totalInputTokens,
	Long totalOutputTokens,
	BigDecimal totalCostUsd,
	OffsetDateTime windowStart,
	OffsetDateTime windowEnd,
	OffsetDateTime lastUpdatedAt,
	DataSourceType dataSource,
	boolean refreshAvailable,
	String message,
	boolean cached
) {
	public UsageSnapshot withCached(boolean cached, String message, OffsetDateTime lastUpdatedAt) {
		return new UsageSnapshot(
			planName,
			used,
			limit,
			remaining,
			unit,
			status,
			startedAt,
			endsAt,
			nextRefreshAt,
			totalRequests,
			totalInputTokens,
			totalOutputTokens,
			totalCostUsd,
			windowStart,
			windowEnd,
			lastUpdatedAt,
			dataSource,
			refreshAvailable,
			message,
			cached
		);
	}
}
