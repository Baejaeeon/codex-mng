package com.toma.codex.usage.web.dto;

public record UsageActivityResponse(
	Long totalRequests,
	Long totalInputTokens,
	Long totalOutputTokens
) {
}
