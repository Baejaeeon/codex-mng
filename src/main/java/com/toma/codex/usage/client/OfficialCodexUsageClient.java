package com.toma.codex.usage.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toma.codex.usage.common.config.CodexUsageProperties;
import com.toma.codex.usage.common.exception.ExternalApiException;
import com.toma.codex.usage.model.AvailabilityStatus;
import com.toma.codex.usage.model.DataSourceType;
import com.toma.codex.usage.model.UsageSnapshot;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@ConditionalOnProperty(prefix = "codex.usage", name = "provider", havingValue = "official")
public class OfficialCodexUsageClient implements CodexUsageClient {

	private final CodexUsageProperties properties;
	private final Clock clock;
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	@Autowired
	public OfficialCodexUsageClient(CodexUsageProperties properties, Clock usageClock) {
		this(properties, usageClock, new RestTemplate(), new ObjectMapper());
	}

	OfficialCodexUsageClient(
		CodexUsageProperties properties,
		Clock usageClock,
		RestTemplate restTemplate,
		ObjectMapper objectMapper
	) {
		this.properties = properties;
		this.clock = usageClock;
		this.restTemplate = restTemplate;
		this.objectMapper = objectMapper;
	}

	@Override
	public UsageSnapshot fetchUsage() {
		String accessToken = resolveAccessToken();
		if (!StringUtils.hasText(accessToken)) {
			throw new ExternalApiException(
				HttpStatus.BAD_REQUEST,
				"MISSING_USAGE_API_KEY",
				"Official usage API requires codex.usage.admin-key (recommended) or codex.usage.api-key."
			);
		}

		OffsetDateTime now = OffsetDateTime.now(clock);
		OffsetDateTime windowStart = now.minusDays(properties.getUsageWindowDays());
		long startTime = windowStart.toEpochSecond();

		JsonNode usageRoot = callUsageApi(startTime, accessToken);
		JsonNode costRoot = callCostsApi(startTime, accessToken);

		long totalRequests = 0;
		long totalInputTokens = 0;
		long totalOutputTokens = 0;
		BigDecimal totalCostUsd = BigDecimal.ZERO;

		for (JsonNode bucket : usageRoot.path("data")) {
			for (JsonNode result : bucket.path("results")) {
				totalRequests += result.path("num_model_requests").asLong(0);
				totalInputTokens += result.path("input_tokens").asLong(0);
				totalOutputTokens += result.path("output_tokens").asLong(0);
			}
		}

		for (JsonNode bucket : costRoot.path("data")) {
			for (JsonNode result : bucket.path("results")) {
				BigDecimal value = result.path("amount").path("value").decimalValue();
				totalCostUsd = totalCostUsd.add(value);
			}
		}

		return new UsageSnapshot(
			"N/A",
			null,
			null,
			null,
			"N/A",
			AvailabilityStatus.AVAILABLE,
			windowStart,
			now,
			now.plusMinutes(1),
			totalRequests,
			totalInputTokens,
			totalOutputTokens,
			totalCostUsd,
			windowStart,
			now,
			now,
			DataSourceType.OFFICIAL_API,
			true,
			"Data sourced from official OpenAI Usage and Costs APIs.",
			false
		);
	}

	private JsonNode callUsageApi(long startTime, String accessToken) {
		URI uri = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
			.path("/v1/organization/usage/completions")
			.queryParam("start_time", startTime)
			.queryParam("bucket_width", "1d")
			.queryParam("limit", properties.getUsageWindowDays())
			.build(true)
			.toUri();
		return execute(uri, accessToken);
	}

	private JsonNode callCostsApi(long startTime, String accessToken) {
		URI uri = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
			.path("/v1/organization/costs")
			.queryParam("start_time", startTime)
			.queryParam("bucket_width", "1d")
			.queryParam("limit", properties.getUsageWindowDays())
			.build(true)
			.toUri();
		return execute(uri, accessToken);
	}

	private JsonNode execute(URI uri, String accessToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(accessToken);
			headers.setContentType(MediaType.APPLICATION_JSON);
			ResponseEntity<String> response = restTemplate.exchange(
				uri,
				HttpMethod.GET,
				new HttpEntity<>(headers),
				String.class
			);
			if (!response.getStatusCode().is2xxSuccessful()) {
				HttpStatus status = HttpStatus.resolve(response.getStatusCode().value());
				if (status == null) {
					status = HttpStatus.BAD_GATEWAY;
				}
				throw new ExternalApiException(
					status,
					"UPSTREAM_ERROR",
					"Failed to fetch official usage data."
				);
			}
			return objectMapper.readTree(response.getBody());
		} catch (ExternalApiException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ExternalApiException(
				HttpStatus.BAD_GATEWAY,
				"UPSTREAM_ERROR",
				"Failed to fetch official usage data: " + ex.getMessage()
			);
		}
	}

	private String resolveAccessToken() {
		if (StringUtils.hasText(properties.getAdminKey())) {
			return properties.getAdminKey();
		}
		if (StringUtils.hasText(properties.getApiKey())) {
			return properties.getApiKey();
		}
		return null;
	}
}
