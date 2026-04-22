package com.toma.codex.usage.common.exception;

import org.springframework.http.HttpStatus;

public class ExternalApiException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final String code;

	public ExternalApiException(HttpStatus httpStatus, String code, String message) {
		super(message);
		this.httpStatus = httpStatus;
		this.code = code;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public String getCode() {
		return code;
	}
}
