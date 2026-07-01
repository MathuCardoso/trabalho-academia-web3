<script setup>
    import Button from "@/components/form/Button.vue";
    import Input from "@/components/form/Input.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import Card from "@/components/ui/Card.vue";
    import Modal from "@/components/modal/Modal.vue";
    import {
        deleteAluna,
        getAluna,
        getAlunas,
        postAluna,
        putAluna,
    } from "@/services/alunaService";
    import { Pencil, Trash2 } from "@lucide/vue";
    import { onMounted, provide, ref } from "vue";
    import Confirm from "@/components/modal/Confirm.vue";
    import Loading from "@/components/icons/Loading.vue";
    import Select from "@/components/form/Select.vue";

    provide("headerTitle", "Listagem de Alunas");

    const pageLoading = ref(true);
    const submitLoading = ref(false);
    const deleteLoading = ref(false);

    const modalAddAluna = ref(false);
    const modalConfirmRemoveAluna = ref(false);
    const selectedAlunaToDelete = ref(null);

    const Aluna = ref({
        id: "",
        nome: "",
        email: "",
        senha: "",
        telefone: "",
        dataNascimento: "",
        cpf: "",
        status: "ATIVO",
    });

    async function prepareUpdate(id) {
        Aluna.value = await getAluna(id);
        modalAddAluna.value = true;
    }

    const errors = ref({});

    async function createOrUpdate() {
        let response = null;
        submitLoading.value = true;
        if (Aluna.value.id) {
            response = await putAluna(Aluna.value);
        } else {
            response = await postAluna(Aluna.value);
        }
        if (!response.success) {
            setTimeout(() => {
                errors.value = { ...response.errors };
                submitLoading.value = false;
            }, 500);
            return;
        }
        closeModalAddAluna();

        alunas.value = await getAlunas();
    }

    async function sendDeleteRequest(id) {
        deleteLoading.value = true;
        await deleteAluna(id);
        alunas.value = await getAlunas();

        modalConfirmRemoveAluna.value = false;
        deleteLoading.value = false;
    }

    function closeModalAddAluna() {
        modalAddAluna.value = false;
        Aluna.value = {};
        errors.value = {};
        submitLoading.value = false;
    }

    function openModalConfirmRemoveAluna(aluna) {
        selectedAlunaToDelete.value = aluna;
        modalConfirmRemoveAluna.value = true;
    }

    const alunas = ref([]);
    onMounted(async () => {
        alunas.value = await getAlunas();
        pageLoading.value = false;
    });
</script>

<template>
    <MainLayout>
        <Loading
            v-if="pageLoading"
            class="absolute right-1/2 top-1/2 -translate-1/2"
        />
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
                <form @submit.prevent="createOrUpdate()">
                    <h1 class="text-center font-bold text-2xl mb-5">
                        Adicionar Aluna
                    </h1>

                    <div class="flex gap-4 mb-5">
                        <Input
                            :model="Aluna.nome"
                            @update-value="Aluna.nome = $event"
                            label="Nome"
                            placeholder="Insira o Nome"
                            :error="errors['nome']"
                        />

                        <Input
                            :model="Aluna.dataNascimento"
                            @update-value="Aluna.dataNascimento = $event"
                            label="Data de Nascimento"
                            placeholder="Ex: 20/11/2005"
                            mask="##/##/####"
                            :error="errors['dataNascimento']"
                        />
                    </div>

                    <div class="flex gap-4 mb-5">
                        <Input
                            :model="Aluna.email"
                            @update-value="Aluna.email = $event"
                            label="Email"
                            placeholder="Ex: email@email.com"
                            :error="errors['email']"
                        />
                        <Input
                            :model="Aluna.senha"
                            @update-value="Aluna.senha = $event"
                            label="Senha"
                            type="password"
                            placeholder="Insira a Senha"
                            :error="errors['senha']"
                        />
                    </div>

                    <div class="flex gap-4 mb-5">
                        <Input
                            :model="Aluna.telefone"
                            @update-value="Aluna.telefone = $event"
                            label="Telefone"
                            placeholder="Ex: 99 99999-9999"
                            mask="## #####-####"
                            :error="errors['telefone']"
                        />
                        <Input
                            :model="Aluna.cpf"
                            @update-value="Aluna.cpf = $event"
                            label="CPF"
                            placeholder="Insira o CPF"
                            mask="###.###.###-##"
                            :error="errors['cpf']"
                        />
                    </div>

                    <div class="gap-4 mb-5">
                        <Select
                            :model="Aluna.status"
                            label="Status"
                            @update-value="Aluna.status = $event"
                            :error="errors['status']"
                        >
                            <option value="ATIVO" selected>ATIVO</option>
                            <option value="INATIVO">INATIVO</option>
                        </Select>
                    </div>

                    <Button
                        type="submit"
                        variant="success"
                        class="font-bold active:scale-98"
                    >
                        <Loading v-if="submitLoading" :size="24" />
                        <template v-else-if="Aluna.id"
                            >Atualizar Aluna</template
                        >
                        <template v-else>Adicionar Aluna</template>
                    </Button>
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
                        :class="{
                            'text-success': a.status == 'ATIVO',
                            'text-danger': a.status == 'INATIVO',
                        }"
                        >{{ a.status }}</span
                    >
                </div>
                <template #footer>
                    <div class="buttons mt-2 flex gap-3">
                        <Button
                            @click="prepareUpdate(a.id)"
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
                    :loading="deleteLoading"
                    title="Exclusão de Aluna"
                    message="Deseja mesmo excluir esta aluna?"
                />
            </Transition>
        </section>
    </MainLayout>
</template>

<style scoped></style>
