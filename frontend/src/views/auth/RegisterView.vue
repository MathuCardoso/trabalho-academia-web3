<script setup>
    import Button from "@/components/form/Button.vue";
    import Input from "@/components/form/Input.vue";
    import { postAluna } from "@/services/alunaService";
    import { ref } from "vue";

    const Aluna = ref({
        nome: "",
        email: "",
        telefone: "",
        dataNascimento: "",
        cpf: "",
        senha: "",
    });

    const response = ref({});
    const errors = ref({});

    async function sendRequest() {
        response.value = await postAluna(Aluna.value);
        console.log(Aluna.value);
        console.log(response.value);
        if (!response.value.success)
            errors.value = { ...response.value.errors };
    }
</script>

<template>
    <div class="form-wrapper">
        <form @submit.prevent="sendRequest">
            <h1>Cadastro</h1>

            <div class="flex gap-4">
                <Input
                    @updateValue="Aluna.nome = $event"
                    label="Nome"
                    placeholder="Insira seu Nome"
                    :error="errors['nome']"
                />

                <Input
                    @updateValue="Aluna.dataNascimento = $event"
                    label="Data de Nascimento"
                    name="dataNascimento"
                    placeholder="Ex: 20/11/2005"
                    mask="##/##/####"
                    :error="errors['dataNascimento']"
                />
            </div>

            <div class="flex gap-4">
                <Input
                    @updateValue="Aluna.email = $event"
                    label="Email"
                    name="email"
                    placeholder="Ex: email@email.com"
                    :error="errors['email']"
                />
                <Input
                    @updateValue="Aluna.senha = $event"
                    label="Senha"
                    name="password"
                    placeholder="Insira sua Senha"
                    :error="errors['senha']"
                />
            </div>

            <div class="flex gap-4">
                <Input
                    @updateValue="Aluna.telefone = $event"
                    label="Telefone"
                    name="telefone"
                    placeholder="Ex: 99 99999-9999"
                    mask="## #####-####"
                    :error="errors['telefone']"
                />
                <Input
                    @updateValue="Aluna.cpf = $event"
                    label="CPF"
                    name="cpf"
                    placeholder="Insira seu CPF"
                    :error="errors['cpf']"
                />
            </div>

            <Button type="submit">Criar Conta</Button>

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
        min-width: 650px;
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
