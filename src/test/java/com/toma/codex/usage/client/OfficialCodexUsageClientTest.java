package com.toma.codex.usage.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toma.codex.usage.common.config.CodexUsageProperties;
import com.toma.codex.usage.common.exception.ExternalApiException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

class OfficialCodexUsageClientTest {

	@Test
	void usesApiKeyWhenAdminKeyIsNotConfigured() {
		CodexUsageProperties properties = new CodexUsageProperties();
		properties.setBaseUrl("https://api.openai.com");
		properties.setUsageWindowDays(1);
		properties.setApiKey("sk-test-api-key");

		Clock clock = Clock.fixed(Instant.parse("2026-04-21T08:00:00Z"), ZoneOffset.UTC);
		RestTemplate restTemplate = new RestTemplate();
		MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

		server.expect(requestTo(org.hamcrest.Matchers.containsString("/v1/organization/usage/completions")))
			.andExpect(method(HttpMethod.GET))
			.andExpect(header("Authorization", "Bearer sk-test-api-key"))
			.andRespond(withSuccess("{\"data\":[]}", MediaType.APPLICATION_JSON));

		server.expect(requestTo(org.hamcrest.Matchers.containsString("/v1/organization/costs")))
			.andExpect(method(HttpMethod.GET))
			.andExpect(header("Authorization", "Bearer sk-test-api-key"))
			.andRespond(withSuccess("{\"data\":[]}", MediaType.APPLICATION_JSON));

		OfficialCodexUsageClient client = new OfficialCodexUsageClient(
			properties,
			clock,
			restTemplate,
			new ObjectMapper()
		);

		var snapshot = client.fetchUsage();

		assertThat(snapshot.totalRequests()).isEqualTo(0L);
		assertThat(snapshot.totalCostUsd()).isNotNull();
		server.verify();
	}

	@Test
	void throwsClearErrorWhenNoUsageApiKeyExists() {
		CodexUsageProperties properties = new CodexUsageProperties();
		properties.setAdminKey("");
		properties.setApiKey("");
		Clock clock = Clock.fixed(Instant.parse("2026-04-21T08:00:00Z"), ZoneOffset.UTC);

		OfficialCodexUsageClient client = new OfficialCodexUsageClient(
			properties,
			clock,
			new RestTemplate(),
			new ObjectMapper()
		);

		assertThatThrownBy(client::fetchUsage)
			.isInstanceOf(ExternalApiException.class)
			.hasMessageContaining("codex.usage.admin-key")
			.hasMessageContaining("codex.usage.api-key");
	}
}
