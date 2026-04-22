package com.toma.codex.usage.web;

import com.toma.codex.usage.model.UsageSnapshot;
import com.toma.codex.usage.web.dto.BillingCycleResponse;
import com.toma.codex.usage.web.dto.CostSummaryResponse;
import com.toma.codex.usage.web.dto.DashboardResponse;
import com.toma.codex.usage.web.dto.PlanResponse;
import com.toma.codex.usage.web.dto.RemainingResponse;
import com.toma.codex.usage.web.dto.SummaryResponse;
import com.toma.codex.usage.web.dto.UsageActivityResponse;
import com.toma.codex.usage.web.dto.UsageMetricResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class UsageResponseMapper {

	public DashboardResponse toDashboard(
		String applicationName,
		UsageSnapshot snapshot,
		String userEmail,
		String scope,
		String mappingStatus
	) {
		return new DashboardResponse(
			applicationName,
			userEmail,
			scope,
			mappingStatus,
			toUsageActivity(snapshot),
			toCosts(snapshot),
			toQueryWindow(snapshot),
			snapshot.dataSource().name(),
			snapshot.cached(),
			snapshot.lastUpdatedAt(),
			snapshot.nextRefreshAt(),
			snapshot.message(),
			snapshot.refreshAvailable()
		);
	}

	public SummaryResponse toSummary(
		String applicationName,
		UsageSnapshot snapshot,
		String userEmail,
		String scope,
		String mappingStatus
	) {
		return new SummaryResponse(
			applicationName,
			userEmail,
			scope,
			mappingStatus,
			toUsageActivity(snapshot),
			toCosts(snapshot),
			toQueryWindow(snapshot),
			snapshot.dataSource().name(),
			snapshot.cached(),
			snapshot.lastUpdatedAt(),
			snapshot.message()
		);
	}

	public PlanResponse toPlan(UsageSnapshot snapshot) {
		return new PlanResponse(
			snapshot.planName(),
			snapshot.dataSource().name(),
			snapshot.message(),
			snapshot.cached(),
			snapshot.lastUpdatedAt()
		);
	}

	public RemainingResponse toRemaining(UsageSnapshot snapshot) {
		return new RemainingResponse(
			snapshot.remaining(),
			snapshot.unit(),
			snapshot.status().name(),
			snapshot.dataSource().name(),
			snapshot.cached(),
			snapshot.lastUpdatedAt(),
			snapshot.message()
		);
	}

	private UsageMetricResponse toUsageMetric(UsageSnapshot snapshot) {
		BigDecimal rate = null;
		if (snapshot.used() != null && snapshot.limit() != null && snapshot.limit().compareTo(BigDecimal.ZERO) > 0) {
			rate = snapshot.used()
				.multiply(BigDecimal.valueOf(100))
				.divide(snapshot.limit(), 2, RoundingMode.HALF_UP);
		}
		return new UsageMetricResponse(
			snapshot.used(),
			snapshot.limit(),
			snapshot.remaining(),
			snapshot.unit(),
			rate,
			snapshot.status().name()
		);
	}

	private BillingCycleResponse toBillingCycle(UsageSnapshot snapshot) {
		return new BillingCycleResponse(snapshot.startedAt(), snapshot.endsAt(), snapshot.nextRefreshAt());
	}

	private BillingCycleResponse toQueryWindow(UsageSnapshot snapshot) {
		return new BillingCycleResponse(snapshot.windowStart(), snapshot.windowEnd(), snapshot.nextRefreshAt());
	}

	private UsageActivityResponse toUsageActivity(UsageSnapshot snapshot) {
		return new UsageActivityResponse(snapshot.totalRequests(), snapshot.totalInputTokens(), snapshot.totalOutputTokens());
	}

	private CostSummaryResponse toCosts(UsageSnapshot snapshot) {
		return new CostSummaryResponse(snapshot.totalCostUsd());
	}
}
