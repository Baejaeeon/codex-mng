package com.toma.codex.usage.web.dto;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
	OffsetDateTime timestamp,
	String code,
	String message,
	String path
) {
}
