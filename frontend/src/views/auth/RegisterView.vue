<script setup>
    import Button from "@/components/form/Button.vue";
    import Input from "@/components/form/Input.vue";
</script>

<template>
    <div class="form-wrapper">
        <form>
            <h1>Cadastro</h1>

            <div class="flex gap-4">
                <Input label="Nome" name="name" placeholder="Insira seu Nome" />

                <Input
                    label="Data de Nascimento"
                    name="dataNascimento"
                    placeholder="Insira sua Data de Nascimento"
                    width="100%"
                    mask="##/##/####"
                />
            </div>

            <div class="flex gap-4">
                <Input
                    label="Email"
                    name="email"
                    placeholder="Insira seu Email"
                />
                <Input
                    label="Senha"
                    name="password"
                    placeholder="Insira sua Senha"
                />
            </div>

            <div class="flex gap-4">
                <Input
                    label="Telefone"
                    name="telefone"
                    placeholder="Insira seu Telefone"
                />
                <Input label="CPF" name="cpf" placeholder="Insira seu CPF" />
            </div>

            <Button content="Entrar" />

            <p>
                Já possui uma conta?
                <span class="text-pink-smooth">
                    <RouterLink to="/login"> Entrar </RouterLink>
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
        width: fit-content;
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
