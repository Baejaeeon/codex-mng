package com.toma.codex.usage.common.config;

import com.toma.codex.usage.common.security.InternalTokenInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@org.springframework.context.annotation.Configuration
public class WebConfig implements WebMvcConfigurer {

	private final InternalTokenInterceptor tokenInterceptor;
	private final CodexUsageProperties properties;

	public WebConfig(InternalTokenInterceptor tokenInterceptor, CodexUsageProperties properties) {
		this.tokenInterceptor = tokenInterceptor;
		this.properties = properties;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(tokenInterceptor).addPathPatterns("/api/codex/**");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOrigins(properties.getAllowedOrigins().toArray(String[]::new))
			.allowedMethods("GET", "POST", "OPTIONS")
			.allowedHeaders("*")
			.allowCredentials(true);
	}
}
