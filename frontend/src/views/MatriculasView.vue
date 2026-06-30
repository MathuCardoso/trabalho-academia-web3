<script setup>
    import Button from "@/components/form/Button.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import Card from "@/components/ui/Card.vue";
    import Modal from "@/components/modal/Modal.vue";
    import {
        deleteMatricula,
        getMatricula,
        getMatriculas,
        postMatricula,
        putMatricula,
    } from "@/services/matriculaService";
    import { Pencil, Trash2 } from "@lucide/vue";
    import { onMounted, provide, ref } from "vue";
    import Confirm from "@/components/modal/Confirm.vue";
    import Loading from "@/components/icons/Loading.vue";
    import Select from "@/components/form/Select.vue";
    import { getAluna, getAlunas } from "@/services/alunaService";
    import { getTreino, getTreinos } from "@/services/treinoService";
    provide("headerTitle", "Listagem de Matrículas");

    const pageLoading = ref(true);
    const submitLoading = ref(false);
    const deleteLoading = ref(false);

    const modalAddMatricula = ref(false);
    const modalConfirmRemoveMatricula = ref(false);
    const selectedMatriculaToDelete = ref(null);

    const Matricula = ref({
        id: "",
        aluna: null,
        treino: null,
        dataInicio: "",
        dataVencimento: "",
        status: "ATIVO",
    });
    const alunaId = ref(null);
    const treinoId = ref(null);

    const matriculas = ref([]);
    const alunas = ref([]);
    const treinos = ref([]);
    onMounted(async () => {
        matriculas.value = await getMatriculas();
        treinos.value = await getTreinos();
        alunas.value = await getAlunas();
        console.log(alunas.value);
        pageLoading.value = false;
    });

    async function prepareUpdate(id) {
        Matricula.value = await getMatricula(id);
        modalAddMatricula.value = true;
    }

    const errors = ref({});
    async function createOrUpdate() {
        let response = null;
        submitLoading.value = true;
        if (Matricula.value.id) {
            response = await putMatricula(Matricula.value);
        } else {
            Matricula.value.aluna = await getAluna(alunaId.value);
            Matricula.value.treino = await getTreino(treinoId.value);
            response = await postMatricula(Matricula.value);
        }
        if (!response.success) {
            console.log(response);
            setTimeout(() => {
                errors.value = { ...response.errors };
                submitLoading.value = false;
            }, 500);
            return;
        }
        closeModalAddMatricula();

        matriculas.value = await getMatricula();
    }

    async function sendDeleteRequest(id) {
        deleteLoading.value = true;
        await deleteMatricula(id);
        matriculas.value = await getMatriculas();

        modalConfirmRemoveMatricula.value = false;
        deleteLoading.value = false;
    }

    function closeModalAddMatricula() {
        modalAddMatricula.value = false;
        Matricula.value = {};
        errors.value = {};
        submitLoading.value = false;
    }

    function openModalConfirmRemoveMatricula(matricula) {
        selectedMatriculaToDelete.value = matricula;
        modalConfirmRemoveMatricula.value = true;
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
                @click="modalAddMatricula = true"
            >
                Adicionar matrícula
            </Button>
        </nav>
        <Transition name="modal" mode="out-in">
            <Modal v-if="modalAddMatricula" @close="closeModalAddMatricula">
                <form @submit.prevent="createOrUpdate()">
                    <h1 class="text-center font-bold text-2xl mb-5">
                        Adicionar Matrícula
                    </h1>

                    <div class="flex gap-4 mb-5">
                        <Select
                            :model="Matricula.aluna?.id || ''"
                            @update-value="alunaId = $event"
                            label="Aluna"
                            :error="errors['aluna']"
                        >
                            <option
                                v-for="a in alunas"
                                :value="a.id"
                                :key="a.id"
                            >
                                {{ a.nome - a.cpf }}
                            </option>
                        </Select>
                        <Select
                            :model="Matricula.treino?.id || ''"
                            @update-value="treinoId = $event"
                            label="Treino"
                            :error="errors['treino']"
                        >
                            <option
                                v-for="t in treinos"
                                :value="t.id"
                                :key="t.id"
                            >
                                {{ t.nome - t.nivel }}
                            </option>
                        </Select>
                    </div>

                    <Button
                        type="submit"
                        variant="success"
                        class="font-bold active:scale-98"
                    >
                        <Loading v-if="submitLoading" :size="24" />
                        <template v-else-if="Matricula.id"
                            >Atualizar Matrícula</template
                        >
                        <template v-else>Adicionar Matrícula</template>
                    </Button>
                </form>
            </Modal>
        </Transition>
        <section
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10 place-items-center"
        >
            <Card v-if="matriculas" v-for="m in matriculas" :key="m.id">
                <template #header>
                    <h3>{{ m.aluna.nome }}</h3>
                    <p class="id">#{{ m.id }}</p>
                </template>
                <div class="card-group flex flex-col">
                    <label>Descrição:</label>
                    <span>{{ m.treino.nome }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Data de Início:</label>
                    <span>{{ m.dataInicio }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Data de Encerramento:</label>
                    <span>{{ m.dataEncerramento }}</span>
                </div>
                <template #footer>
                    <div class="buttons mt-2 flex gap-3">
                        <Button
                            @click="prepareUpdate(m.id)"
                            variant="info"
                            class="hover:-translate-y-1 gap-1"
                        >
                            Editar
                            <template #icon>
                                <Pencil :size="18" />
                            </template>
                        </Button>
                        <Button
                            @click="openModalConfirmRemoveMatricula(m)"
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
                    v-if="modalConfirmRemoveMatricula"
                    @close="modalConfirmRemoveMatricula = false"
                    @confirm="sendDeleteRequest(selectedMatriculaToDelete.id)"
                    :loading="deleteLoading"
                    title="Exclusão de Matricula"
                    message="Deseja mesmo excluir esta matrícula?"
                />
            </Transition>
        </section>
    </MainLayout>
</template>

<style scoped></style>
