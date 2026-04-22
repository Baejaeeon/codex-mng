package com.toma.codex.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class LoginPageResourceTest {

	@Test
	void loginPageContainsGoogleOauthEntryPoint() throws Exception {
		ClassPathResource resource = new ClassPathResource("static/login.html");
		assertThat(resource.exists()).isTrue();
		String content = resource.getContentAsString(StandardCharsets.UTF_8);
		assertThat(content).contains("/oauth2/authorization/google");
		assertThat(content).contains("Sign in with Google");
	}
}
