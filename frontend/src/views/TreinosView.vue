<script setup>
    import Button from "@/components/form/Button.vue";
    import Input from "@/components/form/Input.vue";
    import SearchInput from "@/components/form/SearchInput.vue";
    import Textarea from "@/components/form/Textarea.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import Card from "@/components/ui/Card.vue";
    import Modal from "@/components/modal/Modal.vue";
    import {
        deleteTreino,
        getTreino,
        getTreinos,
        getTreinosPorProfessora,
        postTreino,
        putTreino,
    } from "@/services/treinoService";
    import { Pencil, Trash2 } from "@lucide/vue";
    import { computed, onMounted, provide, ref } from "vue";
    import Confirm from "@/components/modal/Confirm.vue";
    import Loading from "@/components/icons/Loading.vue";
    import Select from "@/components/form/Select.vue";
    import { getProfessoras } from "@/services/professoraService";
    import Errors from "@/components/form/Errors.vue";
    import { useAuthStore } from "@/stores/authStore";
    import { filtrarPorTermo } from "@/composables/useListSearch";

    provide("headerTitle", "Listagem de Treinos");

    const auth = useAuthStore();
    const isAdmin = computed(() => auth.usuario?.perfil === "ADMIN");
    const canEdit = computed(() =>
        ["ADMIN", "PROFESSORA"].includes(auth.usuario?.perfil)
    );
    const canDelete = isAdmin;

    function canEditTreino(treino) {
        return (
            isAdmin.value ||
            treino.professora?.id === auth.usuario?.professoraId
        );
    }

    const pageLoading = ref(true);
    const submitLoading = ref(false);
    const deleteLoading = ref(false);

    const modalAddTreino = ref(false);
    const modalConfirmRemoveTreino = ref(false);
    const selectedTreinoToDelete = ref(null);
    const pesquisa = ref("");

    function treinoVazio() {
        return {
            id: "",
            nome: "",
            descricao: "",
            nivel: "INICIANTE",
            professora: null,
        };
    }

    const Treino = ref(treinoVazio());
    const professoraId = ref(null);
    const treinos = ref([]);
    const treinosFiltrados = computed(() =>
        filtrarPorTermo(treinos.value, pesquisa.value)
    );
    const professoras = ref([]);
    onMounted(async () => {
        try {
            await carregarTreinos();
            if (isAdmin.value) {
                professoras.value = (await getProfessoras()).data;
            } else {
                professoras.value = [
                    {
                        id: auth.usuario?.professoraId,
                        nome: auth.usuario?.nome,
                    },
                ];
                professoraId.value = auth.usuario?.professoraId || null;
            }
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        } finally {
            pageLoading.value = false;
        }
    });

    function openModalAddTreino() {
        if (!isAdmin.value) {
            professoraId.value = auth.usuario?.professoraId || null;
        }
        modalAddTreino.value = true;
    }

    async function prepareUpdate(id) {
        try {
            Treino.value = (await getTreino(id)).data;
            professoraId.value = Treino.value.professora?.id || null;
            modalAddTreino.value = true;
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        }
    }

    const errors = ref({});

    async function createOrUpdate() {
        submitLoading.value = true;

        const professoraSelecionada = isAdmin.value
            ? professoraId.value || Treino.value.professora?.id
            : auth.usuario?.professoraId;

        Treino.value.professora = professoraSelecionada
            ? { id: Number(professoraSelecionada) }
            : null;

        try {
            if (Treino.value.id) {
                await putTreino(Treino.value);
            } else {
                await postTreino(Treino.value);
            }
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
            submitLoading.value = false;
            return;
        }

        closeModalAddTreino();

        await carregarTreinos();
    }

    async function sendDeleteRequest(id) {
        deleteLoading.value = true;
        try {
            await deleteTreino(id);
            await carregarTreinos();
            modalConfirmRemoveTreino.value = false;
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
            modalConfirmRemoveTreino.value = false;
        } finally {
            deleteLoading.value = false;
        }
    }

    function closeModalAddTreino() {
        modalAddTreino.value = false;
        Treino.value = treinoVazio();
        professoraId.value = null;
        errors.value = {};
        submitLoading.value = false;
    }

    function openModalConfirmRemoveTreino(treino) {
        selectedTreinoToDelete.value = treino;
        modalConfirmRemoveTreino.value = true;
    }

    async function carregarTreinos() {
        treinos.value = isAdmin.value
            ? (await getTreinos()).data
            : (await getTreinosPorProfessora(auth.usuario.professoraId)).data;
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
                v-if="canEdit"
                bg="var(--color-success)"
                color="black"
                class="add-button hover:scale-102"
                @click="openModalAddTreino"
            >
                Adicionar treino
            </Button>
            <SearchInput
                :model="pesquisa"
                @update-value="pesquisa = $event"
                placeholder="Pesquisar treinos"
            />
        </nav>
        <Errors :error="errors['geral']" />
        <Transition name="modal" mode="out-in">
            <Modal v-if="modalAddTreino" @close="closeModalAddTreino">
                <form @submit.prevent="createOrUpdate()">
                    <h1 class="text-center font-bold text-2xl mb-5">
                        Adicionar Treino
                    </h1>
                    <Errors :error="errors['geral']" />

                    <div class="flex gap-4 mb-5">
                        <Input
                            :model="Treino.nome"
                            @update-value="Treino.nome = $event"
                            label="Nome"
                            placeholder="Insira o Nome do treino"
                            :error="errors['nome']"
                        />
                        <Textarea
                            :model="Treino.descricao"
                            @update-value="Treino.descricao = $event"
                            label="Descrição"
                            placeholder="Descreva o treino"
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
                            :model="professoraId || Treino.professora?.id || ''"
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
            <Card v-for="t in treinosFiltrados" :key="t.id">
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
                    <span>{{ t.professora?.nome }}</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Status:</label>
                    <span
                        class="font-bold"
                        :class="{
                            'text-success': t.status === 'ATIVO',
                            'text-danger': t.status === 'INATIVO',
                        }"
                    >
                        {{ t.status }}
                    </span>
                </div>
                <template #footer>
                    <div class="buttons mt-2 flex gap-3">
                        <Button
                            v-if="canEdit && canEditTreino(t)"
                            @click="prepareUpdate(t.id)"
                            variant="info"
                            class="card-action-button hover:-translate-y-1 gap-1"
                        >
                            Editar
                            <template #icon>
                                <Pencil :size="18" />
                            </template>
                        </Button>
                        <Button
                            v-if="canDelete"
                            @click="openModalConfirmRemoveTreino(t)"
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

<style scoped>
    .list-toolbar {
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
    }

    .add-button {
        max-width: 210px;
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
