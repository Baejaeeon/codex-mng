package com.toma.codex.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
public class SecurityConfig {

	private final FrontendAppProperties frontendAppProperties;

	public SecurityConfig(FrontendAppProperties frontendAppProperties) {
		this.frontendAppProperties = frontendAppProperties;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		String frontendLoginUrl = frontendAppProperties.loginUrl();
		String frontendDashboardUrl = frontendAppProperties.dashboardUrl();

		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/",
					"/oauth2/**",
					"/login/oauth2/**",
					"/actuator/health",
					"/favicon.ico"
				).permitAll()
				.requestMatchers("/api/**").authenticated()
				.anyRequest().permitAll()
			)
			.exceptionHandling(ex -> ex
				.defaultAuthenticationEntryPointFor(
					new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
					new AntPathRequestMatcher("/api/**")
				)
				.defaultAuthenticationEntryPointFor(
					new LoginUrlAuthenticationEntryPoint(frontendLoginUrl),
					new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
				)
			)
			.oauth2Login(login -> login
				.defaultSuccessUrl(frontendDashboardUrl, true)
				.failureUrl(frontendLoginUrl + "?error=true")
			)
			.logout(logout -> logout
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
				.logoutUrl("/logout")
				.logoutSuccessUrl(frontendLoginUrl + "?logout=true")
			)
			.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

		return http.build();
	}
}
