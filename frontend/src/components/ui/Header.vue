<script setup lang="ts">
    import ProfileModal from "@/components/modal/ProfileModal.vue";
    import { LogOut, UserRoundPen } from "@lucide/vue";
    import { computed, inject, ref } from "vue";
    import { useRouter } from "vue-router";
    import { useAuthStore } from "@/stores/authStore";
    import UserIcon from "../icons/UserIcon.vue";

    const title = inject("headerTitle");
    const description = inject("headerDescription", null);
    const auth = useAuthStore();
    const router = useRouter();
    const modalProfile = ref(false);
    const canEditProfile = computed(() =>
        ["ALUNA", "PROFESSORA"].includes(auth.usuario?.perfil)
    );

    function logout() {
        auth.logout();
        router.replace("/login");
    }
</script>

<template>
    <header>
        <div class="welcome">
            <h2>{{ title }}</h2>
            <p v-if="description">{{ description }}</p>
        </div>
        <nav>
            <ul>
                <li class="user">
                    <UserIcon />
                    <span>{{ auth.usuario?.nome }}</span>
                </li>
                <li v-if="canEditProfile">
                    <button
                        type="button"
                        title="Editar meus dados"
                        aria-label="Editar meus dados"
                        @click="modalProfile = true"
                    >
                        <UserRoundPen :size="19" />
                        <span>Meus dados</span>
                    </button>
                </li>
                <li>
                    <button
                        type="button"
                        title="Encerrar sessão"
                        aria-label="Encerrar sessão"
                        @click="logout"
                    >
                        <LogOut :size="19" />
                        <span>Sair</span>
                    </button>
                </li>
            </ul>
        </nav>
        <ProfileModal v-if="modalProfile" @close="modalProfile = false" />
    </header>
</template>

<style scoped>
    header {
        width: 100%;
        padding-left: calc(var(--spacing-sidebar) + 5vw);
        padding-right: 5vw;
        position: fixed;
        top: 0;
        left: 0;
        height: var(--spacing-header);
        display: flex;
        z-index: 20;
        display: flex;
        justify-content: space-between;
        align-items: center;
        backdrop-filter: blur(4px);

        h2 {
            font-size: 24px;
            font-weight: bold;
        }
    }

    ul {
        display: flex;
        align-items: center;
        gap: 15px;
    }

    li.user,
    button {
        display: flex;
        align-items: center;
        gap: 8px;
    }

    button {
        padding: 8px 10px;
        color: var(--color-pink-accent);
        cursor: pointer;
    }

    button:hover {
        color: white;
    }

    @media (max-width: 768px) {
        li.user span {
            display: none;
        }
    }
</style>
