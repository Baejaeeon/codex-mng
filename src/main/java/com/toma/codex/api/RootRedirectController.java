package com.toma.codex.api;

import com.toma.codex.auth.config.FrontendAppProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

	private final FrontendAppProperties frontendAppProperties;

	public RootRedirectController(FrontendAppProperties frontendAppProperties) {
		this.frontendAppProperties = frontendAppProperties;
	}

	@GetMapping("/")
	public String root() {
		return "redirect:" + frontendAppProperties.loginUrl();
	}
}
