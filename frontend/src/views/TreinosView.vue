<script setup>
    import Button from "@/components/form/Button.vue";
    import Input from "@/components/form/Input.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import Card from "@/components/ui/Card.vue";
    import Modal from "@/components/modal/Modal.vue";
    import {
        deleteTreino,
        getTreino,
        getTreinos,
        postTreino,
        putTreino,
    } from "@/services/treinoService";
    import { Pencil, Trash2 } from "@lucide/vue";
    import { computed, onMounted, provide, ref } from "vue";
    import Confirm from "@/components/modal/Confirm.vue";
    import Loading from "@/components/icons/Loading.vue";
    import Select from "@/components/form/Select.vue";
    import {
        getProfessora,
        getProfessoras,
    } from "@/services/professoraService";

    provide("headerTitle", "Listagem de Treinos");

    const pageLoading = ref(true);
    const submitLoading = ref(false);
    const deleteLoading = ref(false);

    const modalAddTreino = ref(false);
    const modalConfirmRemoveTreino = ref(false);
    const selectedTreinoToDelete = ref(null);

    const Treino = ref({
        id: "",
        nome: "",
        descricao: "",
        nivel: "INICIANTE",
        professora: null,
    });
    const professoraId = ref(null);
    const treinos = ref([]);
    const professoras = ref([]);
    onMounted(async () => {
        treinos.value = await getTreinos();
        professoras.value = await getProfessoras();
        pageLoading.value = false;
    });

    async function prepareUpdate(id) {
        Treino.value = await getTreino(id);
        modalAddTreino.value = true;
    }

    const errors = ref({});

    async function createOrUpdate() {
        let response = null;
        submitLoading.value = true;
        if (Treino.value.id) {
            response = await putTreino(Treino.value);
        } else {
            Treino.value.professora = await getProfessora(professoraId.value);
            response = await postTreino(Treino.value);
        }
        if (!response.success) {
            console.log(response);
            setTimeout(() => {
                errors.value = { ...response.errors };
                submitLoading.value = false;
            }, 500);
            return;
        }
        closeModalAddTreino();

        treinos.value = await getTreinos();
    }

    async function sendDeleteRequest(id) {
        deleteLoading.value = true;
        await deleteTreino(id);
        treinos.value = await getTreinos();

        modalConfirmRemoveTreino.value = false;
        deleteLoading.value = false;
    }

    function closeModalAddTreino() {
        modalAddTreino.value = false;
        Treino.value = {};
        errors.value = {};
        submitLoading.value = false;
    }

    function openModalConfirmRemoveTreino(treino) {
        selectedTreinoToDelete.value = treino;
        modalConfirmRemoveTreino.value = true;
    }
</script>

<template>
    <MainLayout>
        <Loading
            v-if="pageLoading"
            class="absolute right-1/2 top-1/2 -translate-1/2"
        />
        <nav class="mb-4 grid grid-cols-5 place-items-center">
            <Button
                bg="var(--color-success)"
                color="black"
                class="hover:scale-102"
                @click="modalAddTreino = true"
            >
                Adicionar treino
            </Button>
        </nav>
        <Transition name="modal" mode="out-in">
            <Modal v-if="modalAddTreino" @close="closeModalAddTreino">
                <form @submit.prevent="createOrUpdate()">
                    <h1 class="text-center font-bold text-2xl mb-5">
                        Adicionar Treino
                    </h1>

                    <div class="flex gap-4 mb-5">
                        <Input
                            :model="Treino.nome"
                            @update-value="Treino.nome = $event"
                            label="Nome"
                            placeholder="Insira o Nome do treino"
                            :error="errors['nome']"
                        />
                        <Input
                            :model="Treino.descricao"
                            @update-value="Treino.descricao = $event"
                            label="Descricao"
                            :error="errors['descricao']"
                        />
                    </div>

                    <div class="flex gap-4 mb-5">
                        <Select
                            :model="Treino.nivel"
                            @update-value="Treino.nivel = $event"
                            label="Nível"
                            :error="errors['nivel']"
                        >
                            <option value="INICIANTE" selected>
                                INICIANTE
                            </option>
                            <option value="INTERMEDIARIO">INTERMEDIÁRIO</option>
                            <option value="AVANCADO">AVANÇADO</option>
                        </Select>
                        <Select
                            :model="Treino.professora?.id || ''"
                            @update-value="professoraId = $event"
                            label="Professora"
                            :error="errors['professora']"
                        >
                            <option
                                v-for="p in professoras"
                                :value="p.id"
                                :key="p.id"
                            >
                                {{ p.nome }}
                            </option>
                        </Select>
                    </div>

                    <Button
                        type="submit"
                        variant="success"
                        class="font-bold active:scale-98"
                    >
                        <Loading v-if="submitLoading" :size="24" />
                        <template v-else-if="Treino.id"
                            >Atualizar Treino</template
                        >
                        <template v-else>Adicionar Treino</template>
                    </Button>
                </form>
            </Modal>
        </Transition>
        <section
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10 place-items-center"
        >
            <Card v-if="treinos" v-for="t in treinos" :key="t.id">
                <template #header>
                    <h3>{{ t.nome }}</h3>
                    <p class="id">#{{ t.id }}</p>
                </template>
                <div class="card-group flex flex-col">
                    <label>Descrição:</label>
                    <span>{{ t.descricao }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Nível:</label>
                    <span
                        :class="{
                            'text-danger': t.nivel == 'AVANCADO',
                            'text-yellow-400': t.nivel == 'INTERMEDIARIO',
                            'text-success': t.nivel == 'INICIANTE',
                        }"
                        >{{ t.nivel }}</span
                    >
                </div>
                <div class="card-group flex flex-col">
                    <label>Professora:</label>
                    <span>{{ t.professora.nome }}</span>
                </div>
                <template #footer>
                    <div class="buttons mt-2 flex gap-3">
                        <Button
                            @click="prepareUpdate(t.id)"
                            variant="info"
                            class="hover:-translate-y-1 gap-1"
                        >
                            Editar
                            <template #icon>
                                <Pencil :size="18" />
                            </template>
                        </Button>
                        <Button
                            @click="openModalConfirmRemoveTreino(t)"
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
                    v-if="modalConfirmRemoveTreino"
                    @close="modalConfirmRemoveTreino = false"
                    @confirm="sendDeleteRequest(selectedTreinoToDelete.id)"
                    :loading="deleteLoading"
                    title="Exclusão de Treino"
                    message="Deseja mesmo excluir este Treino?"
                />
            </Transition>
        </section>
    </MainLayout>
</template>

<style scoped></style>
