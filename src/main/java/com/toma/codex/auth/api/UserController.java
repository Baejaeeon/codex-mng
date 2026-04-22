package com.toma.codex.auth.api;

import java.util.Locale;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

	@GetMapping("/me")
	public UserProfileResponse getMe(@AuthenticationPrincipal OAuth2User user) {
		String name = stringAttr(user, "name");
		String email = normalizeEmail(stringAttr(user, "email"));
		String picture = stringAttr(user, "picture");
		return new UserProfileResponse(name, email, picture);
	}

	private String stringAttr(OAuth2User user, String key) {
		if (user == null || user.getAttributes().get(key) == null) {
			return null;
		}
		return String.valueOf(user.getAttributes().get(key));
	}

	private String normalizeEmail(String email) {
		if (email == null || email.isBlank()) {
			return null;
		}
		return email.trim().toLowerCase(Locale.ROOT);
	}

	public record UserProfileResponse(String name, String email, String pictureUrl) {
	}
}
