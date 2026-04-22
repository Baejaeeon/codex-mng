package com.toma.codex.usage.web;

import com.toma.codex.usage.common.exception.ExternalApiException;
import com.toma.codex.usage.common.exception.UnauthorizedException;
import com.toma.codex.usage.web.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiErrorResponse> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
			new ApiErrorResponse(OffsetDateTime.now(), "UNAUTHORIZED", ex.getMessage(), request.getRequestURI())
		);
	}

	@ExceptionHandler(ExternalApiException.class)
	public ResponseEntity<ApiErrorResponse> handleExternalApi(ExternalApiException ex, HttpServletRequest request) {
		return ResponseEntity.status(ex.getHttpStatus()).body(
			new ApiErrorResponse(OffsetDateTime.now(), ex.getCode(), ex.getMessage(), request.getRequestURI())
		);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
			new ApiErrorResponse(
				OffsetDateTime.now(),
				"INTERNAL_SERVER_ERROR",
				"Unexpected server error occurred.",
				request.getRequestURI()
			)
		);
	}
}
