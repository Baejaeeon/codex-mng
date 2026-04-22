package com.toma.codex.usage.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.toma.codex.CodexMngApplication;
import com.toma.codex.usage.client.CodexUsageClient;
import com.toma.codex.usage.common.exception.ExternalApiException;
import com.toma.codex.usage.model.AvailabilityStatus;
import com.toma.codex.usage.model.DataSourceType;
import com.toma.codex.usage.model.UsageSnapshot;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = CodexMngApplication.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CodexUsageApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CodexUsageClient usageClient;

	@Test
	@Order(1)
	void returnsExternalApiErrorWhenProviderFails() throws Exception {
		when(usageClient.fetchUsage()).thenThrow(new ExternalApiException(HttpStatus.BAD_GATEWAY, "UPSTREAM_ERROR", "upstream failed"));

		mockMvc.perform(post("/api/codex/dashboard/refresh")
				.with(oauth2Login().attributes(attrs -> attrs.put("email", "qa@sample.com"))))
			.andExpect(status().isBadGateway())
			.andExpect(jsonPath("$.code").value("UPSTREAM_ERROR"));
	}

	@Test
	@Order(2)
	void returnsDashboardPayloadWhenAuthorized() throws Exception {
		when(usageClient.fetchUsage()).thenReturn(buildSnapshot());

		mockMvc.perform(get("/api/codex/dashboard")
				.with(oauth2Login().attributes(attrs -> attrs.put("email", "qa@sample.com"))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.applicationName").value("codex-mng"))
			.andExpect(jsonPath("$.userEmail").value("qa@sample.com"))
			.andExpect(jsonPath("$.scope").value("ORGANIZATION_SHARED"))
			.andExpect(jsonPath("$.usageActivity.totalRequests").value(120))
			.andExpect(jsonPath("$.usageActivity.totalInputTokens").value(2000))
			.andExpect(jsonPath("$.costs.totalCostUsd").value(12.34))
			.andExpect(jsonPath("$.dataSource").value("OFFICIAL_API"));
	}

	@Test
	@Order(3)
	void returnsUnauthorizedForUnauthenticatedApi() throws Exception {
		mockMvc.perform(get("/api/codex/dashboard"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@Order(4)
	void redirectsRootToFrontendLogin() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost:3000/login"));
	}

	@Test
	@Order(5)
	void returnsCurrentUserProfile() throws Exception {
		mockMvc.perform(get("/api/me")
				.with(oauth2Login().attributes(attrs -> {
					attrs.put("email", "dev@sample.com");
					attrs.put("name", "Dev User");
					attrs.put("picture", "https://example.com/a.png");
				})))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("dev@sample.com"))
			.andExpect(jsonPath("$.name").value("Dev User"));
	}

	private UsageSnapshot buildSnapshot() {
		return new UsageSnapshot(
			"PRO",
			BigDecimal.valueOf(10),
			BigDecimal.valueOf(100),
			BigDecimal.valueOf(90),
			"TOKENS",
			AvailabilityStatus.AVAILABLE,
			OffsetDateTime.parse("2026-04-01T00:00:00Z"),
			OffsetDateTime.parse("2026-04-30T23:59:59Z"),
			OffsetDateTime.parse("2026-05-01T00:00:00Z"),
			120L,
			2000L,
			850L,
			BigDecimal.valueOf(12.34),
			OffsetDateTime.parse("2026-04-01T00:00:00Z"),
			OffsetDateTime.parse("2026-04-30T23:59:59Z"),
			OffsetDateTime.parse("2026-04-20T00:00:00Z"),
			DataSourceType.OFFICIAL_API,
			true,
			"ok",
			false
		);
	}
}
