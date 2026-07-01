<script setup>
    import Button from "@/components/form/Button.vue";
    import Input from "@/components/form/Input.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import Card from "@/components/ui/Card.vue";
    import Modal from "@/components/modal/Modal.vue";
    import {
        deleteProfessora,
        getProfessora,
        getProfessoras,
        postProfessora,
        putProfessora,
    } from "@/services/professoraService";
    import { Pencil, Trash2 } from "@lucide/vue";
    import { onMounted, provide, ref } from "vue";
    import Confirm from "@/components/modal/Confirm.vue";
    import Loading from "@/components/icons/Loading.vue";
    import Select from "@/components/form/Select.vue";

    provide("headerTitle", "Listagem de Professoras");

    const pageLoading = ref(true);
    const submitLoading = ref(false);
    const deleteLoading = ref(false);

    const modalAddProfessora = ref(false);
    const modalConfirmRemoveProfessora = ref(false);
    const selectedProfessoraToDelete = ref(null);

    const Professora = ref({
        id: "",
        nome: "",
        email: "",
        especialidade: "",
        status: "ATIVO",
    });

    async function prepareUpdate(id) {
        Professora.value = await getProfessora(id);
        modalAddProfessora.value = true;
    }

    const response = ref({});
    const errors = ref({});

    async function createOrUpdate() {
        submitLoading.value = true;
        if (Professora.value.id) {
            response.value = await putProfessora(Professora.value);
        } else {
            response.value = await postProfessora(Professora.value);
        }
        if (!response.value.success) {
            setTimeout(() => {
                errors.value = { ...response.value.errors };
                submitLoading.value = false;
            }, 500);
            return;
        }
        closeModalAddProfessora();

        professoras.value = await getProfessoras();
    }

    async function sendDeleteRequest(id) {
        deleteLoading.value = true;
        response.value = await deleteProfessora(id);
        professoras.value = await getProfessoras();

        modalConfirmRemoveProfessora.value = false;
        deleteLoading.value = false;
    }

    function closeModalAddProfessora() {
        modalAddProfessora.value = false;
        Professora.value = {};
        errors.value = {};
        submitLoading.value = false;
    }

    function openModalConfirmRemoveProfessora(professora) {
        selectedProfessoraToDelete.value = professora;
        modalConfirmRemoveProfessora.value = true;
    }

    const professoras = ref([]);
    onMounted(async () => {
        professoras.value = await getProfessoras();
        pageLoading.value = false;
    });
</script>

<template>
    <MainLayout>
        <Loading
            v-if="pageLoading"
            class="absolute right-1/2 top-1/2 -translate-1/2"
        />
        <nav class="mb-4 grid grid-cols-4 place-items-center">
            <Button
                bg="var(--color-success)"
                color="black"
                class="hover:scale-102"
                @click="modalAddProfessora = true"
            >
                Adicionar professora
            </Button>
        </nav>
        <Transition name="modal" mode="out-in">
            <Modal v-if="modalAddProfessora" @close="closeModalAddProfessora">
                <form @submit.prevent="createOrUpdate()">
                    <h1 class="text-center font-bold text-2xl mb-5">
                        Adicionar Professora
                    </h1>

                    <div class="flex gap-4 mb-5">
                        <Input
                            :model="Professora.nome"
                            @update-value="Professora.nome = $event"
                            label="Nome"
                            placeholder="Insira o Nome"
                            :error="errors['nome']"
                        />
                        <Input
                            :model="Professora.email"
                            @update-value="Professora.email = $event"
                            label="Email"
                            placeholder="Ex: email@email.com"
                            :error="errors['email']"
                        />
                    </div>

                    <div class="flex gap-4 mb-5">
                        <Input
                            :model="Professora.especialidade"
                            @update-value="Professora.especialidade = $event"
                            label="Especialidade"
                            type="text"
                            placeholder="Insira a especialidade"
                            :error="errors['especialidade']"
                        />
                        <Select
                            :model="Professora.status"
                            @update-value="Professora.status = $event"
                            label="Status"
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
                        <template v-else-if="Professora.id"
                            >Atualizar Professora</template
                        >
                        <template v-else>Adicionar Professora</template>
                    </Button>
                </form>
            </Modal>
        </Transition>
        <section
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10 place-items-center"
        >
            <Card v-if="professoras" v-for="p in professoras" :key="p.id">
                <template #header>
                    <h3>{{ p.nome }}</h3>
                    <p class="id">#{{ p.id }}</p>
                </template>
                <div class="card-group flex flex-col">
                    <label>Email:</label>
                    <span>{{ p.email }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Especialidade:</label>
                    <span>{{ p.especialidade }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Status:</label>
                    <span
                        class="font-bold"
                        :class="{
                            'text-success': p.status == 'ATIVO',
                            'text-danger': p.status == 'INATIVO',
                        }"
                        >{{ p.status }}</span
                    >
                </div>
                <template #footer>
                    <div class="buttons mt-2 flex gap-3">
                        <Button
                            @click="prepareUpdate(p.id)"
                            variant="info"
                            class="hover:-translate-y-1 gap-1"
                        >
                            Editar
                            <template #icon>
                                <Pencil :size="18" />
                            </template>
                        </Button>
                        <Button
                            @click="openModalConfirmRemoveProfessora(p)"
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
                    v-if="modalConfirmRemoveProfessora"
                    @close="modalConfirmRemoveProfessora = false"
                    @confirm="sendDeleteRequest(selectedProfessoraToDelete.id)"
                    :loading="deleteLoading"
                    title="Exclusão de Professora"
                    message="Deseja mesmo excluir esta professora?"
                />
            </Transition>
        </section>
    </MainLayout>
</template>

<style scoped></style>
