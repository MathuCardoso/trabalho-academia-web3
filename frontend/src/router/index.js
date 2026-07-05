import { createRouter, createWebHistory } from "vue-router";
import routes from "./routes";
import { useAuthStore } from '@/stores/authStore';

const router = createRouter({
    history: createWebHistory(),
    routes: routes,
});

router.beforeEach((to, from) => {
    const auth = useAuthStore();

    if ((to.meta.requiresAuth && !auth.isAuthenticated)) {
        return "/login";
    }
});

router.afterEach((to) => {
    document.title = `${to.meta.title || "Dashboard"} | Bella Fit & Women`;
});

export default router;
