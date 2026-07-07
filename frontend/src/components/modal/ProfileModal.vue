<script setup>
    import Button from "@/components/form/Button.vue";
    import Errors from "@/components/form/Errors.vue";
    import Input from "@/components/form/Input.vue";
    import Loading from "@/components/icons/Loading.vue";
    import { getAluna, putMeuPerfilAluna } from "@/services/alunaService";
    import {
        getProfessora,
        putMeuPerfilProfessora,
    } from "@/services/professoraService";
    import { useAuthStore } from "@/stores/authStore";
    import { computed, onMounted, ref } from "vue";
    import { useRouter } from "vue-router";
    import Modal from "./Modal.vue";

    const emit = defineEmits(["close"]);
    const auth = useAuthStore();
    const router = useRouter();
    const isAluna = computed(() => auth.usuario?.perfil === "ALUNA");
    const loading = ref(true);
    const submitLoading = ref(false);
    const errors = ref({});
    const perfil = ref({ senha: "" });

    onMounted(async () => {
        try {
            perfil.value = isAluna.value
                ? (await getAluna(auth.usuario.alunaId)).data
                : (await getProfessora(auth.usuario.professoraId)).data;
            perfil.value.senha = "";
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        } finally {
            loading.value = false;
        }
    });

    async function salvar() {
        submitLoading.value = true;
        errors.value = {};

        const payload = { ...perfil.value };
        delete payload.id;
        delete payload.status;
        delete payload.usuarioId;
        if (!payload.senha) delete payload.senha;

        try {
            const response = isAluna.value
                ? await putMeuPerfilAluna(auth.usuario.alunaId, payload)
                : await putMeuPerfilProfessora(
                      auth.usuario.professoraId,
                      payload
                  );

            const novoLogin = isAluna.value
                ? response.data.cpf
                : response.data.cref;

            if (novoLogin !== auth.usuario.login) {
                auth.logout();
                await router.replace("/login");
                return;
            }

            auth.atualizarUsuario({ nome: response.data.nome });
            emit("close");
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        } finally {
            submitLoading.value = false;
        }
    }
</script>

<template>
    <Teleport to="body">
        <Modal @close="emit('close')">
            <Loading v-if="loading" class="profile-loading" />

            <form v-else @submit.prevent="salvar">
                <h1 class="profile-title">Meus dados</h1>
                <Errors :error="errors['geral']" />

            <div class="form-row">
                <Input
                    :model="perfil.nome"
                    @update-value="perfil.nome = $event"
                    label="Nome"
                    :error="errors['nome']"
                />
                <Input
                    :model="perfil.email"
                    @update-value="perfil.email = $event"
                    label="E-mail"
                    type="email"
                    :error="errors['email']"
                />
            </div>

            <div v-if="isAluna" class="form-row">
                <Input
                    :model="perfil.telefone"
                    @update-value="perfil.telefone = $event"
                    label="Telefone"
                    mask="## #####-####"
                    :error="errors['telefone']"
                />
                <Input
                    :model="perfil.cpf"
                    @update-value="perfil.cpf = $event"
                    label="CPF"
                    mask="###.###.###-##"
                    :error="errors['cpf']"
                />
            </div>

            <div v-if="isAluna" class="form-row">
                <Input
                    :model="perfil.dataNascimento"
                    @update-value="perfil.dataNascimento = $event"
                    label="Data de nascimento"
                    type="date"
                    :error="errors['dataNascimento']"
                />
            </div>

            <div v-else class="form-row">
                <Input
                    :model="perfil.cref"
                    @update-value="perfil.cref = $event.toUpperCase()"
                    label="CREF"
                    :error="errors['cref']"
                />
                <Input
                    :model="perfil.especialidade"
                    @update-value="perfil.especialidade = $event"
                    label="Especialidade"
                    :error="errors['especialidade']"
                />
            </div>

            <div class="form-row">
                <Input
                    :model="perfil.senha"
                    @update-value="perfil.senha = $event"
                    label="Senha"
                    type="password"
                    placeholder="Deixe em branco para manter a senha atual"
                    :error="errors['senha']"
                />
            </div>

                <Button type="submit" variant="success">
                    <Loading v-if="submitLoading" :size="22" />
                    <template v-else>Salvar alterações</template>
                </Button>
            </form>
        </Modal>
    </Teleport>
</template>

<style scoped>
    .profile-loading {
        margin: 80px auto;
    }

    .profile-title {
        margin-bottom: 20px;
        text-align: center;
        font-size: 1.5rem;
        font-weight: 700;
    }

    form {
        width: min(620px, 85vw);
    }

    .form-row {
        margin-bottom: 16px;
        display: flex;
        gap: 16px;
    }

    @media (max-width: 640px) {
        .form-row {
            flex-direction: column;
        }
    }
</style>
