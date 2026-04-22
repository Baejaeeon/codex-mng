const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export function backendUrl(path) {
	return `${API_BASE_URL}${path}`;
}

export async function fetchJson(path, options = {}) {
	const response = await fetch(backendUrl(path), {
		credentials: "include",
		...options
	});

	if (response.status === 401) {
		const error = new Error("UNAUTHORIZED");
		error.status = 401;
		throw error;
	}

	if (!response.ok) {
		let message = "Request failed";
		try {
			const json = await response.json();
			message = json.message ?? message;
		} catch {
			// Keep default message when body is not JSON.
		}
		const error = new Error(message);
		error.status = response.status;
		throw error;
	}

	return response.json();
}

export function redirectToGoogleLogin() {
	window.location.href = backendUrl("/oauth2/authorization/google");
}

export function redirectToLogout() {
	window.location.href = backendUrl("/logout");
}
