import { createApp } from "vue";
import { createRouter, createWebHistory } from "vue-router";
import App from "./App.vue";
import LoginPage from "./pages/LoginPage.vue";
import DashboardPage from "./pages/DashboardPage.vue";
import "./styles.css";

const router = createRouter({
	history: createWebHistory(),
	routes: [
		{ path: "/", redirect: "/login" },
		{ path: "/login", component: LoginPage },
		{ path: "/dashboard", component: DashboardPage }
	]
});

createApp(App).use(router).mount("#app");
