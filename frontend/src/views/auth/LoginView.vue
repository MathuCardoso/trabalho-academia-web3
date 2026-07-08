<script setup>
    import Button from "@/components/form/Button.vue";
    import Errors from "@/components/form/Errors.vue";
    import Input from "@/components/form/Input.vue";
    import Loading from "@/components/icons/Loading.vue";
    import router from "@/router";
    import { useAuthStore } from "@/stores/authStore";
    import { ref } from "vue";
    import { Dumbbell } from "@lucide/vue";

    const auth = useAuthStore();
    const login = ref("");
    const senha = ref("");

    const errors = ref({});
    const loading = ref(false);

    async function sendRequest() {
        try {
            loading.value = true;
            errors.value = {};
            await auth.login(login.value, senha.value);
            router.push("/");
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        } finally {
            loading.value = false;
        }
    }
</script>

<template>
    <main class="login-page">
        <section class="brand-panel">
            <div class="brand-content">
                <Dumbbell :size="42" />
                <h1>Bella Fit <span>& Women</span></h1>
                <p>Gestão simples para uma rotina de treinos mais forte.</p>
            </div>
        </section>

        <section class="form-panel">
            <form @submit.prevent="sendRequest()">
                <div class="form-heading">
                    <h2>Entrar</h2>
                    <p>Acesse sua conta para continuar.</p>
                </div>

                <Errors :error="errors['geral']" />

                <Input
                    @update-value="
                        login =
                            $event.includes('-') && $event.includes('/')
                                ? $event.toUpperCase()
                                : $event
                    "
                    :model="login"
                    label="Login"
                    placeholder="Insira seu login"
                    mask="['######-L/LL', '###.###.###-##', 'LLLLLL']"
                    mask-tokens="L:[A-Za-z]"
                    :error="errors['login']"
                />

                <Input
                    @update-value="senha = $event"
                    :model="senha"
                    label="Senha"
                    placeholder="Insira sua senha"
                    type="password"
                    :error="errors['senha']"
                />

                <Button type="submit" :disabled="loading">
                    <Loading v-if="loading" :size="22" />
                    <template v-else>Entrar</template>
                </Button>
            </form>
        </section>
    </main>
</template>

<style scoped>
    .login-page {
        min-height: 100vh;
        display: grid;
        grid-template-columns: minmax(0, 1.15fr) minmax(420px, 0.85fr);
    }

    .brand-panel,
    .form-panel {
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .brand-panel {
        padding: 48px;
        background-color: #1a1d26;
        border-right: 1px solid var(--color-text);
    }

    .brand-content {
        max-width: 580px;
    }

    .brand-content > svg {
        margin-bottom: 24px;
        color: var(--color-cyan-accent);
    }

    .brand-content h1 {
        font-family: "Playfair Display", serif;
        font-size: 5rem;
        font-weight: 700;
        line-height: 1;
    }

    .brand-content h1 span {
        display: block;
        color: var(--color-pink-accent);
    }

    .brand-content p {
        max-width: 470px;
        margin-top: 24px;
        color: #b6bac5;
        font-size: 1.1rem;
        line-height: 1.6;
    }

    .form-panel {
        padding: 32px;
    }

    form {
        width: min(100%, 440px);
        display: flex;
        flex-direction: column;
        gap: 16px;
    }

    .form-heading {
        margin-bottom: 12px;
    }

    .form-heading h2 {
        font-size: 2rem;
        font-weight: bold;
    }

    .form-heading p {
        margin-top: 6px;
        color: #b6bac5;
    }

    @media (max-width: 820px) {
        .login-page {
            grid-template-columns: 1fr;
        }

        .brand-panel {
            min-height: 34vh;
            padding: 36px 24px;
            justify-content: flex-start;
            border-right: 0;
            border-bottom: 1px solid var(--color-text);
        }

        .brand-content h1 {
            font-size: 2.75rem;
        }

        .brand-content p {
            margin-top: 14px;
        }

        .form-panel {
            padding: 36px 24px;
        }
    }
</style>
