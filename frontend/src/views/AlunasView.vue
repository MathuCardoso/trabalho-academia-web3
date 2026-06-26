<script setup>
    import Button from "@/components/form/Button.vue";
    import Input from "@/components/form/Input.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import Card from "@/components/ui/Card.vue";
    import Modal from "@/components/modal/Modal.vue";
    import {
        deleteAluna,
        getAlunas,
        postAluna,
    } from "@/services/alunasService";
    import { Pencil, Trash2 } from "@lucide/vue";
    import { onMounted, provide, ref } from "vue";
    import Confirm from "@/components/modal/Confirm.vue";

    provide("headerTitle", "Listagem de Alunas");

    const modalAddAluna = ref(false);
    const modalConfirmRemoveAluna = ref(false);
    const selectedAlunaToDelete = ref(null);

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

    async function sendPostRequest() {
        response.value = await postAluna(Aluna.value);
        if (!response.value.success) {
            errors.value = { ...response.value.errors };
            return;
        }
        modalAddAluna.value = false;
        errors.value = {};
        alunas.value = await getAlunas();
    }

    async function sendDeleteRequest(id) {
        response.value = await deleteAluna(id);
        alunas.value = await getAlunas();

        modalConfirmRemoveAluna.value = false;
    }

    function closeModalAddAluna() {
        modalAddAluna.value = false;
        errors.value = {};
    }

    function openModalConfirmRemoveAluna(aluna) {
        selectedAlunaToDelete.value = aluna;
        modalConfirmRemoveAluna.value = true;
    }

    const alunas = ref([]);
    onMounted(async () => {
        alunas.value = await getAlunas();
        console.log(alunas.value);
    });
</script>

<template>
    <MainLayout>
        <nav class="mb-4 grid grid-cols-6 place-items-center">
            <Button
                bg="var(--color-success)"
                color="black"
                class="hover:scale-102"
                @click="modalAddAluna = true"
            >
                Adicionar aluna
            </Button>
        </nav>
        <Transition name="modal" mode="out-in">
            <Modal v-if="modalAddAluna" @close="closeModalAddAluna">
                <form @submit.prevent="sendPostRequest">
                    <h1 class="text-center font-bold text-2xl mb-5">
                        Adicionar Aluna
                    </h1>

                    <div class="flex gap-4 mb-5">
                        <Input
                            @update-value="Aluna.nome = $event"
                            label="Nome"
                            placeholder="Insira o Nome"
                            :error="errors['nome']"
                        />

                        <Input
                            @update-value="Aluna.dataNascimento = $event"
                            label="Data de Nascimento"
                            name="dataNascimento"
                            placeholder="Ex: 20/11/2005"
                            mask="##/##/####"
                            :error="errors['dataNascimento']"
                        />
                    </div>

                    <div class="flex gap-4 mb-5">
                        <Input
                            @update-value="Aluna.email = $event"
                            label="Email"
                            name="email"
                            placeholder="Ex: email@email.com"
                            :error="errors['email']"
                        />
                        <Input
                            @update-value="Aluna.senha = $event"
                            label="Senha"
                            type="password"
                            placeholder="Insira a Senha"
                            :error="errors['senha']"
                        />
                    </div>

                    <div class="flex gap-4 mb-5">
                        <Input
                            @update-value="Aluna.telefone = $event"
                            label="Telefone"
                            name="telefone"
                            placeholder="Ex: 99 99999-9999"
                            mask="## #####-####"
                            :error="errors['telefone']"
                        />
                        <Input
                            @update-value="Aluna.cpf = $event"
                            label="CPF"
                            name="cpf"
                            placeholder="Insira o CPF"
                            :error="errors['cpf']"
                        />
                    </div>

                    <Button
                        type="submit"
                        variant="success"
                        class="font-bold active:scale-98"
                        >Adicionar Aluna</Button
                    >
                </form>
            </Modal>
        </Transition>

        <section
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10 place-items-center"
        >
            <Card v-if="alunas" v-for="a in alunas" :key="a.id">
                <template #header>
                    <h3>{{ a.nome }}</h3>
                    <p class="id">#{{ a.id }}</p>
                </template>
                <div class="card-group flex flex-col">
                    <label>Email:</label>
                    <span>{{ a.email }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>CPF:</label>
                    <span>{{ a.cpf }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Telefone:</label>
                    <span>{{ a.telefone }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Data de Nascimento:</label>
                    <span>{{ a.dataNascimento }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Status:</label>
                    <span
                        class="font-bold"
                        :class="{ 'text-success': a.status == 'ATIVO' }"
                        >{{ a.status }}</span
                    >
                </div>
                <template #footer>
                    <div class="buttons mt-2 flex gap-3">
                        <Button
                            variant="info"
                            class="hover:-translate-y-1 gap-1"
                        >
                            Editar
                            <template #icon>
                                <Pencil :size="18" />
                            </template>
                        </Button>
                        <Button
                            @click="openModalConfirmRemoveAluna(a)"
                            variant="danger"
                            class="hover:-translate-y-1 gap-1"
                        >
                            Excluir
                            <template #icon>
                                <Trash2 :size="18" />
                            </template>
                        </Button>
                    </div>
                </template>
            </Card>
            <Transition name="modal" mode="out-in">
                <Confirm
                    v-if="modalConfirmRemoveAluna"
                    @close="modalConfirmRemoveAluna = false"
                    @confirm="sendDeleteRequest(selectedAlunaToDelete.id)"
                    title="Exclusão de Aluna"
                    message="Deseja mesmo excluir esta aluna?"
                />
            </Transition>
        </section>
    </MainLayout>
</template>

<style scoped></style>
