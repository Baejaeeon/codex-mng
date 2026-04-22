<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { fetchJson, redirectToLogout } from "../lib/api";

const router = useRouter();
const loading = ref(true);
const refreshing = ref(false);
const errorMessage = ref("");
const me = ref(null);
const dashboard = ref(null);

const usageRateLabel = computed(() => {
	const rate = dashboard.value?.usage?.usageRate;
	return rate == null ? "-" : `${Number(rate).toFixed(2)}%`;
});

const summary = computed(() => {
	const usage = dashboard.value?.usageActivity;
	if (!usage) {
		return [];
	}
	return [
		{ label: "Total Requests", value: usage.totalRequests ?? "-" },
		{ label: "Input Tokens", value: usage.totalInputTokens ?? "-" },
		{ label: "Output Tokens", value: usage.totalOutputTokens ?? "-" },
		{ label: "Total Cost (USD)", value: dashboard.value?.costs?.totalCostUsd ?? "-" }
	];
});

async function loadData(forceRefresh = false) {
	errorMessage.value = "";
	const endpoint = forceRefresh ? "/api/codex/dashboard/refresh" : "/api/codex/dashboard";

	try {
		const [profile, usage] = await Promise.all([
			fetchJson("/api/me"),
			fetchJson(endpoint, { method: forceRefresh ? "POST" : "GET" })
		]);
		me.value = profile;
		dashboard.value = usage;
	} catch (error) {
		if (error.status === 401) {
			await router.replace("/login");
			return;
		}
		errorMessage.value = error.message;
	} finally {
		loading.value = false;
		refreshing.value = false;
	}
}

function refresh() {
	refreshing.value = true;
	loadData(true);
}

onMounted(() => {
	loadData(false);
});
</script>

<template>
	<main class="page">
		<section class="card">
			<header class="header">
				<div>
					<h1>Codex Usage Dashboard</h1>
					<p>{{ dashboard?.message ?? "Loading usage data..." }}</p>
					<p v-if="me?.email" class="meta">Signed in as {{ me.email }}</p>
				</div>
				<div class="actions">
					<button :disabled="loading || refreshing" @click="refresh">
						{{ refreshing ? "Refreshing..." : "Refresh" }}
					</button>
					<button class="secondary" @click="redirectToLogout">Logout</button>
				</div>
			</header>

			<p v-if="errorMessage" class="message">{{ errorMessage }}</p>

			<div v-if="loading" class="skeleton">Loading dashboard...</div>

			<template v-else-if="dashboard">
				<div class="grid">
					<article class="tile">
						<h2>Current Plan</h2>
						<p class="big">{{ dashboard.planName ?? "UNKNOWN" }}</p>
					</article>
					<article class="tile">
						<h2>Usage In Current Cycle</h2>
						<p class="big">{{ usageRateLabel }}</p>
					</article>
					<article class="tile">
						<h2>Scope</h2>
						<p class="big">{{ dashboard.scope ?? "-" }}</p>
					</article>
				</div>

				<div class="grid">
					<article v-for="item in summary" :key="item.label" class="tile">
						<h2>{{ item.label }}</h2>
						<p class="big">{{ item.value }}</p>
					</article>
				</div>
			</template>
		</section>
	</main>
</template>
