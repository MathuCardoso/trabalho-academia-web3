import { createRouter, createWebHistory } from "vue-router";
import routes from "./routes";

const router = createRouter({
    history: createWebHistory(),
    routes: routes,
});

router.afterEach((to) => {
    document.title = `${to.meta.title || "Dashboard"} | Bella Fit & Women`;
});

export default router;
