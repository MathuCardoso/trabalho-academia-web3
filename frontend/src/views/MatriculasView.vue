<script setup>
    import Button from "@/components/form/Button.vue";
    import Input from "@/components/form/Input.vue";
    import SearchInput from "@/components/form/SearchInput.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import Card from "@/components/ui/Card.vue";
    import Modal from "@/components/modal/Modal.vue";
    import {
        deleteMatricula,
        getMatricula,
        getMatriculas,
        getMatriculasPorAluna,
        postMatricula,
        putMatricula,
    } from "@/services/matriculaService";
    import { Pencil, Trash2 } from "@lucide/vue";
    import { computed, onMounted, provide, ref } from "vue";
    import Confirm from "@/components/modal/Confirm.vue";
    import Loading from "@/components/icons/Loading.vue";
    import Select from "@/components/form/Select.vue";
    import { getAlunas } from "@/services/alunaService";
    import { getTreinos } from "@/services/treinoService";
    import Errors from "@/components/form/Errors.vue";
    import { useAuthStore } from "@/stores/authStore";
    import { filtrarPorTermo } from "@/composables/useListSearch";
    provide("headerTitle", "Listagem de Matrículas");

    const auth = useAuthStore();
    const canManage = computed(() => auth.usuario?.perfil === "ADMIN");

    const pageLoading = ref(true);
    const submitLoading = ref(false);
    const deleteLoading = ref(false);

    const modalAddMatricula = ref(false);
    const modalConfirmRemoveMatricula = ref(false);
    const selectedMatriculaToDelete = ref(null);
    const pesquisa = ref("");

    function matriculaVazia() {
        return {
            id: "",
            aluna: null,
            treino: null,
            dataInicio: "",
            dataVencimento: "",
            status: "ATIVA",
        };
    }

    const Matricula = ref(matriculaVazia());
    const alunaId = ref(null);
    const treinoId = ref(null);

    const matriculas = ref([]);
    const matriculasFiltradas = computed(() =>
        filtrarPorTermo(matriculas.value, pesquisa.value)
    );
    const alunas = ref([]);
    const treinos = ref([]);
    onMounted(async () => {
        try {
            await carregarMatriculas();
            treinos.value = (await getTreinos()).data;
            if (canManage.value) {
                alunas.value = (await getAlunas()).data;
            }
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        } finally {
            pageLoading.value = false;
        }
    });

    async function carregarMatriculas() {
        if (auth.usuario?.perfil === "ALUNA" && auth.usuario?.alunaId) {
            matriculas.value = (
                await getMatriculasPorAluna(auth.usuario.alunaId)
            ).data;
            return;
        }

        matriculas.value = (await getMatriculas()).data;
    }

    async function prepareUpdate(id) {
        try {
            Matricula.value = (await getMatricula(id)).data;
            alunaId.value = Matricula.value.aluna?.id || null;
            treinoId.value = Matricula.value.treino?.id || null;
            modalAddMatricula.value = true;
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        }
    }

    const errors = ref({});
    async function createOrUpdate() {
        submitLoading.value = true;

        const alunaSelecionada = alunaId.value || Matricula.value.aluna?.id;
        const treinoSelecionado = treinoId.value || Matricula.value.treino?.id;

        Matricula.value.aluna = alunaSelecionada
            ? { id: Number(alunaSelecionada) }
            : null;
        Matricula.value.treino = treinoSelecionado
            ? { id: Number(treinoSelecionado) }
            : null;

        try {
            if (Matricula.value.id) {
                await putMatricula(Matricula.value);
            } else {
                await postMatricula(Matricula.value);
            }
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
            submitLoading.value = false;
            return;
        }

        closeModalAddMatricula();

        await carregarMatriculas();
    }

    async function sendDeleteRequest(id) {
        deleteLoading.value = true;
        try {
            await deleteMatricula(id);
            await carregarMatriculas();
            modalConfirmRemoveMatricula.value = false;
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
            modalConfirmRemoveMatricula.value = false;
        } finally {
            deleteLoading.value = false;
        }
    }

    function closeModalAddMatricula() {
        modalAddMatricula.value = false;
        Matricula.value = matriculaVazia();
        alunaId.value = null;
        treinoId.value = null;
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
        <nav class="list-toolbar">
            <Button
                v-if="canManage"
                bg="var(--color-success)"
                color="black"
                class="add-button hover:scale-102"
                @click="modalAddMatricula = true"
            >
                Adicionar matrícula
            </Button>
            <SearchInput
                :model="pesquisa"
                @update-value="pesquisa = $event"
                placeholder="Pesquisar matrículas"
            />
        </nav>
        <Errors :error="errors['geral']" />
        <Transition name="modal" mode="out-in">
            <Modal v-if="modalAddMatricula" @close="closeModalAddMatricula">
                <form @submit.prevent="createOrUpdate()">
                    <h1 class="text-center font-bold text-2xl mb-5">
                        Adicionar Matrícula
                    </h1>
                    <Errors :error="errors['geral']" />

                    <div class="flex gap-4 mb-5">
                        <Select
                            :model="alunaId || Matricula.aluna?.id || ''"
                            @update-value="alunaId = $event"
                            label="Aluna"
                            :error="errors['aluna']"
                        >
                            <option
                                v-for="a in alunas"
                                :value="a.id"
                                :key="a.id"
                            >
                                {{ a.nome }} - {{ a.cpf }}
                            </option>
                        </Select>
                        <Select
                            :model="treinoId || Matricula.treino?.id || ''"
                            @update-value="treinoId = $event"
                            label="Treino"
                            :error="errors['treino']"
                        >
                            <option
                                v-for="t in treinos"
                                :value="t.id"
                                :key="t.id"
                            >
                                {{ t.nome }} - {{ t.nivel }}
                            </option>
                        </Select>
                    </div>

                    <div class="flex gap-4 mb-5">
                        <Input
                            :model="Matricula.dataInicio"
                            @update-value="Matricula.dataInicio = $event"
                            label="Data de Início"
                            type="date"
                            :error="errors['dataInicio']"
                        />
                        <Input
                            :model="Matricula.dataVencimento"
                            @update-value="Matricula.dataVencimento = $event"
                            label="Data de Vencimento"
                            type="date"
                            :error="errors['dataVencimento']"
                        />
                    </div>

                    <div class="flex gap-4 mb-5">
                        <Select
                            :model="Matricula.status"
                            @update-value="Matricula.status = $event"
                            label="Status"
                            :error="errors['status']"
                        >
                            <option value="ATIVA" selected>ATIVA</option>
                            <option value="VENCIDA">VENCIDA</option>
                            <option value="CANCELADA">CANCELADA</option>
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
            <Card v-for="m in matriculasFiltradas" :key="m.id">
                <template #header>
                    <h3>{{ m.aluna?.nome }}</h3>
                    <p class="id">#{{ m.id }}</p>
                </template>
                <div class="card-group flex flex-col">
                    <label>Descrição:</label>
                    <span>{{ m.treino?.nome }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Data de Início:</label>
                    <span>{{ m.dataInicio }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Data de Vencimento:</label>
                    <span>{{ m.dataVencimento }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Status:</label>
                    <span
                        class="font-bold"
                        :class="{
                            'text-success': m.status === 'ATIVA',
                            'text-danger': ['VENCIDA', 'CANCELADA'].includes(
                                m.status
                            ),
                        }"
                    >
                        {{ m.status }}
                    </span>
                </div>
                <template #footer>
                    <div class="buttons mt-2 flex gap-3">
                        <Button
                            v-if="canManage"
                            @click="prepareUpdate(m.id)"
                            variant="info"
                            class="card-action-button hover:-translate-y-1 gap-1"
                        >
                            Editar
                            <template #icon>
                                <Pencil :size="18" />
                            </template>
                        </Button>
                        <Button
                            v-if="canManage"
                            @click="openModalConfirmRemoveMatricula(m)"
                            variant="danger"
                            class="card-action-button hover:-translate-y-1 gap-1"
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

<style scoped>
    .list-toolbar {
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
    }

    .add-button {
        max-width: 230px;
    }

    .card-action-button {
        width: 128px;
        min-height: 44px;
        flex: 0 0 128px;
    }

    @media (max-width: 640px) {
        .list-toolbar {
            align-items: stretch;
            flex-direction: column;
        }
    }
</style>
