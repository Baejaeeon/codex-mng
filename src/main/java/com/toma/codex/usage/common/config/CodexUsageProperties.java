package com.toma.codex.usage.common.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.toma.codex.usage.model.UserScopeMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "codex.usage")
public class CodexUsageProperties {

	@NotBlank
	private String provider = "manual";

	@NotBlank
	private String baseUrl = "https://api.openai.com";

	private String adminKey = "";

	private String apiKey = "";

	@NotNull
	private Duration timeout = Duration.ofSeconds(5);

	@Min(0)
	private int retryCount = 1;

	@Min(1)
	private int usageWindowDays = 7;

	@NotNull
	private Duration cacheTtl = Duration.ofSeconds(60);

	private List<String> allowedOrigins = new ArrayList<>(List.of("http://localhost:8080"));

	private String internalToken = "";

	@NotNull
	private UserScopeMode userScopeMode = UserScopeMode.ORGANIZATION_SHARED;

	private Map<String, String> userProjectMapping = new HashMap<>();

	@Valid
	@NotNull
	private Manual manual = new Manual();

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Duration getTimeout() {
		return timeout;
	}

	public void setTimeout(Duration timeout) {
		this.timeout = timeout;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public int getUsageWindowDays() {
		return usageWindowDays;
	}

	public void setUsageWindowDays(int usageWindowDays) {
		this.usageWindowDays = usageWindowDays;
	}

	public Duration getCacheTtl() {
		return cacheTtl;
	}

	public void setCacheTtl(Duration cacheTtl) {
		this.cacheTtl = cacheTtl;
	}

	public List<String> getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(List<String> allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	public String getInternalToken() {
		return internalToken;
	}

	public void setInternalToken(String internalToken) {
		this.internalToken = internalToken;
	}

	public UserScopeMode getUserScopeMode() {
		return userScopeMode;
	}

	public void setUserScopeMode(UserScopeMode userScopeMode) {
		this.userScopeMode = userScopeMode;
	}

	public Map<String, String> getUserProjectMapping() {
		return userProjectMapping;
	}

	public void setUserProjectMapping(Map<String, String> userProjectMapping) {
		this.userProjectMapping = userProjectMapping;
	}

	public String getAdminKey() {
		return adminKey;
	}

	public void setAdminKey(String adminKey) {
		this.adminKey = adminKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Manual getManual() {
		return manual;
	}

	public void setManual(Manual manual) {
		this.manual = manual;
	}

	public static class Manual {

		@NotBlank
		private String planName = "UNKNOWN";

		@NotBlank
		private String unit = "UNKNOWN";

		@NotBlank
		private String message = "Manual provider is active. Connect an official usage API to load real values.";

		public String getPlanName() {
			return planName;
		}

		public void setPlanName(String planName) {
			this.planName = planName;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
