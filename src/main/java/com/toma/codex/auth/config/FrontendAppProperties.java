package com.toma.codex.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.frontend")
public class FrontendAppProperties {

	private String baseUrl = "http://localhost:3000";

	private String loginPath = "/login";

	private String dashboardPath = "/dashboard";

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getLoginPath() {
		return loginPath;
	}

	public void setLoginPath(String loginPath) {
		this.loginPath = loginPath;
	}

	public String getDashboardPath() {
		return dashboardPath;
	}

	public void setDashboardPath(String dashboardPath) {
		this.dashboardPath = dashboardPath;
	}

	public String loginUrl() {
		return baseUrl + loginPath;
	}

	public String dashboardUrl() {
		return baseUrl + dashboardPath;
	}
}
