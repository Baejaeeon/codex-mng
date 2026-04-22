package com.toma.codex.usage.common.security;

import com.toma.codex.usage.common.config.CodexUsageProperties;
import com.toma.codex.usage.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InternalTokenInterceptor implements HandlerInterceptor {

	private static final String HEADER_NAME = "X-Internal-Token";

	private final CodexUsageProperties properties;

	public InternalTokenInterceptor(CodexUsageProperties properties) {
		this.properties = properties;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String configuredToken = properties.getInternalToken();
		if (!StringUtils.hasText(configuredToken)) {
			return true;
		}
		String requestToken = request.getHeader(HEADER_NAME);
		if (!configuredToken.equals(requestToken)) {
			throw new UnauthorizedException("Missing or invalid X-Internal-Token.");
		}
		return true;
	}
}
