<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { fetchJson, redirectToGoogleLogin } from "../lib/api";

const route = useRoute();
const router = useRouter();
const checkingSession = ref(true);
const errorMessage = ref("");

onMounted(async () => {
	if (route.query.error) {
		errorMessage.value = "Google OAuth login failed. Check client settings and try again.";
	}
	if (route.query.logout) {
		errorMessage.value = "Signed out successfully.";
	}

	try {
		await fetchJson("/api/me");
		await router.replace("/dashboard");
	} catch (error) {
		if (error.status !== 401) {
			errorMessage.value = error.message;
		}
	} finally {
		checkingSession.value = false;
	}
});
</script>

<template>
	<main class="page">
		<section class="card login-card">
			<h1>codex-mng</h1>
			<p>Sign in with Google to access your usage dashboard.</p>
			<p v-if="errorMessage" class="message">{{ errorMessage }}</p>
			<button :disabled="checkingSession" @click="redirectToGoogleLogin">
				Sign in with Google
			</button>
		</section>
	</main>
</template>
