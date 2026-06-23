import "./assets/main.css";

import { createApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";

import router from "./router";

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.mount("#app");

router.beforeResolve((to, from) => {
    if (!document.startViewTransition || to.path === from.path) {
        return true;
    }

    return new Promise((resolve) => {
        document.startViewTransition(async () => {
            resolve(true);
        });
    });
});
