package com.toma.codex.usage.api;

import com.toma.codex.usage.service.CodexUsageService;
import com.toma.codex.usage.web.dto.DashboardResponse;
import com.toma.codex.usage.web.dto.PlanResponse;
import com.toma.codex.usage.web.dto.RemainingResponse;
import com.toma.codex.usage.web.dto.SummaryResponse;
import java.util.Locale;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/codex")
public class CodexUsageController {

	private final CodexUsageService usageService;

	public CodexUsageController(CodexUsageService usageService) {
		this.usageService = usageService;
	}

	@GetMapping("/usage/summary")
	public SummaryResponse getSummary(Authentication authentication) {
		return usageService.getSummary(extractEmail(authentication));
	}

	@GetMapping("/usage/plan")
	public PlanResponse getPlan(Authentication authentication) {
		return usageService.getPlan(extractEmail(authentication));
	}

	@GetMapping("/usage/remaining")
	public RemainingResponse getRemaining(Authentication authentication) {
		return usageService.getRemaining(extractEmail(authentication));
	}

	@GetMapping("/dashboard")
	public DashboardResponse getDashboard(Authentication authentication) {
		return usageService.getDashboard(extractEmail(authentication));
	}

	@PostMapping("/dashboard/refresh")
	public DashboardResponse refreshDashboard(Authentication authentication) {
		return usageService.refreshDashboard(extractEmail(authentication));
	}

	private String extractEmail(Authentication authentication) {
		if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User user)) {
			return null;
		}
		Object email = user.getAttributes().get("email");
		if (!(email instanceof String value) || value.isBlank()) {
			return null;
		}
		return value.trim().toLowerCase(Locale.ROOT);
	}
}
