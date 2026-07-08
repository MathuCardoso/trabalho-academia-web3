<script setup>
    import { computed } from "vue";
    import { useRoute } from "vue-router";
    import { useAuthStore } from "@/stores/authStore";

    const route = useRoute();
    const auth = useAuthStore();

    const sideBarOptions = [
        {
            name: "Home",
            route: "/",
            roles: ["ADMIN", "PROFESSORA", "ALUNA"],
        },
        {
            name: "Alunas",
            route: "/alunas",
            roles: ["ADMIN"],
        },
        {
            name: "Professoras",
            route: "/professoras",
            roles: ["ADMIN"],
        },
        {
            name: "Treinos",
            route: "/treinos",
            roles: ["ADMIN", "PROFESSORA"],
        },
        {
            name: "Matrículas",
            route: "/matriculas",
            roles: ["ADMIN"],
        },
        {
            name: "Frequência",
            route: "/frequencias",
            roles: ["ADMIN", "ALUNA"],
        },
    ];

    const visibleOptions = computed(() => {
        const perfil = auth.usuario?.perfil;

        return sideBarOptions.filter((option) =>
            option.roles.includes(perfil)
        );
    });
</script>

<template>
    <div class="sidebar">
        <div class="brand">
            <h1 class="flex flex-col">
                Bella Fit
                <span class="text-pink-accent"> & Women </span>
            </h1>
        </div>

        <nav>
            <ul>
                <RouterLink
                    v-for="s in visibleOptions"
                    :key="s.name"
                    :to="s.route"
                >
                    <li :class="{ active: route.fullPath === s.route }">
                        {{ s.name }}
                    </li>
                </RouterLink>
            </ul>
        </nav>
    </div>
</template>

<style scoped>
    .sidebar {
        position: fixed;
        left: 0;
        top: 0;
        background-color: var(--color-card);
        border-right: 1px solid var(--color-pink-accent-shadow);
        border-top-right-radius: 0px;
        width: var(--spacing-sidebar);
        height: 100vh;
        z-index: 50;
    }

    nav {
        display: flex;
        flex-direction: column;
        align-items: center;
        margin-top: 20px;
    }

    nav ul {
        display: flex;
        flex-direction: column;
        width: 100%;
        padding: 0 20px;
        gap: 15px;
    }

    nav ul li {
        padding: 15px;
        border-radius: 15px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        z-index: 1;
        color: #8c8c9a;
        transition: color 0.1s ease-out;
    }

    nav ul li::before {
        content: "";
        position: absolute;
        inset: 0;
        background: linear-gradient(
            135deg,
            rgba(228, 62, 97, 0.25) 0%,
            rgba(228, 62, 97, 0.05) 100%
        );
        backdrop-filter: blur(10px);
        opacity: 0;
        transition: opacity 0.3s ease-out;
        z-index: -1;
    }

    nav ul li:hover::before,
    nav ul li.active::before {
        opacity: 1;
    }

    nav ul li:hover,
    nav ul li.active {
        color: #e43e61;
    }

    .brand {
        display: flex;
        justify-content: center;
        padding-top: 20px;
        font-size: 24px;
        font-family: "Playfair Display", serif;
    }
</style>
