<script setup>
    import Button from "@/components/form/Button.vue";
    import Errors from "@/components/form/Errors.vue";
    import Input from "@/components/form/Input.vue";
    import router from "@/router";
    import { useAuthStore } from "@/stores/authStore";
    import { ref } from "vue";

    const auth = useAuthStore();
    const login = ref("admin");
    const senha = ref("admin123");

    const errors = ref({});

    async function sendRequest() {
        try {
            errors.value = {};
            await auth.login(login.value, senha.value);
            router.push("/");
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        }
    }
</script>

<template>
    <div class="form-wrapper">
        <form @submit.prevent="sendRequest()">
            <h1>Login</h1>

            <Errors :error="errors['geral']" />

            <Input
                @update-value="login = $event"
                :model="login"
                label="Login"
                placeholder="Insira seu login"
                :error="errors['login']"
            />

            <Input
                @update-value="senha = $event"
                :model="senha"
                label="Senha"
                placeholder="Insira sua Senha"
                type="password"
                :error="errors['senha']"
            />

            <Button type="submit">Entrar</Button>
        </form>
    </div>
</template>

<style scoped>
    .form-wrapper {
        height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
    }
    form {
        width: 450px;
        height: fit-content;
        display: flex;
        flex-direction: column;
        background-color: var(--color-card);
        border: 2px solid var(--color-text);
        padding: 20px;
        border-radius: var(--radius-card);
        gap: 12px;
    }
    h1 {
        color: white;
        font-size: 32px;
        font-weight: bold;
        text-align: center;
    }
</style>
