<script setup>
    import Button from "@/components/form/Button.vue";
    import Input from "@/components/form/Input.vue";
</script>

<template>
    <div class="form-wrapper">
        <form>
            <h1>Login</h1>

            <Input label="Email" name="email" placeholder="Insira seu Email" />

            <Input
                label="Senha"
                name="password"
                placeholder="Insira sua Senha"
            />

            <Button content="Entrar" />

            <p>
                Não possui uma conta?
                <span class="text-pink-smooth">
                    <RouterLink to="/cadastro"> Cadastrar-se </RouterLink>
                </span>
            </p>
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
