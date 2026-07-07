<script setup>
    import Button from "@/components/form/Button.vue";
    import SearchInput from "@/components/form/SearchInput.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import Card from "@/components/ui/Card.vue";
    import Modal from "@/components/modal/Modal.vue";
    import Confirm from "@/components/modal/Confirm.vue";
    import Loading from "@/components/icons/Loading.vue";
    import Select from "@/components/form/Select.vue";
    import Errors from "@/components/form/Errors.vue";
    import { getAlunas } from "@/services/alunaService";
    import {
        deleteFrequencia,
        getFrequencias,
        getFrequenciasPorAluna,
        registrarCheckin,
    } from "@/services/frequenciaService";
    import { useAuthStore } from "@/stores/authStore";
    import { Trash2, Clock3 } from "@lucide/vue";
    import { computed, onMounted, provide, ref } from "vue";
    import { filtrarPorTermo } from "@/composables/useListSearch";

    provide("headerTitle", "Frequência");

    const auth = useAuthStore();

    const isAdmin = computed(() => auth.usuario?.perfil === "ADMIN");
    const isAluna = computed(() => auth.usuario?.perfil === "ALUNA");

    const pageLoading = ref(true);
    const checkinLoading = ref(false);
    const deleteLoading = ref(false);

    const errors = ref({});
    const successMessage = ref("");

    const frequencias = ref([]);
    const alunas = ref([]);

    const modalCheckin = ref(false);
    const modalConfirmRemoveFrequencia = ref(false);

    const selectedAlunaId = ref("");
    const selectedFrequenciaToDelete = ref(null);
    const pesquisa = ref("");
    const frequenciasFiltradas = computed(() =>
        filtrarPorTermo(frequencias.value, pesquisa.value)
    );

    onMounted(async () => {
        try {
            await carregarFrequencias();

            if (isAdmin.value) {
                alunas.value = (await getAlunas()).data;
            }
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        } finally {
            pageLoading.value = false;
        }
    });

    async function carregarFrequencias() {
        if (isAluna.value) {
            if (!auth.usuario?.alunaId) {
                frequencias.value = [];
                errors.value = {
                    geral: "Usuário não possui aluna vinculada.",
                };
                return;
            }

            frequencias.value = (
                await getFrequenciasPorAluna(auth.usuario.alunaId)
            ).data;
            return;
        }

        frequencias.value = (await getFrequencias()).data;
    }

    async function registrarMeuCheckin() {
        errors.value = {};
        successMessage.value = "";
        checkinLoading.value = true;

        try {
            await registrarCheckin(auth.usuario.alunaId);
            successMessage.value = "Check-in registrado com sucesso.";
            await carregarFrequencias();
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        } finally {
            checkinLoading.value = false;
        }
    }

    async function registrarCheckinAdmin() {
        errors.value = {};
        successMessage.value = "";

        if (!selectedAlunaId.value) {
            errors.value = {
                aluna: "Selecione uma aluna.",
            };
            return;
        }

        checkinLoading.value = true;

        try {
            await registrarCheckin(selectedAlunaId.value);
            successMessage.value = "Check-in registrado com sucesso.";
            modalCheckin.value = false;
            selectedAlunaId.value = "";
            await carregarFrequencias();
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        } finally {
            checkinLoading.value = false;
        }
    }

    async function sendDeleteRequest(id) {
        deleteLoading.value = true;
        errors.value = {};
        successMessage.value = "";

        try {
            await deleteFrequencia(id);
            successMessage.value = "Frequência removida com sucesso.";
            await carregarFrequencias();
            modalConfirmRemoveFrequencia.value = false;
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
            modalConfirmRemoveFrequencia.value = false;
        } finally {
            deleteLoading.value = false;
        }
    }

    function openModalConfirmRemoveFrequencia(frequencia) {
        selectedFrequenciaToDelete.value = frequencia;
        modalConfirmRemoveFrequencia.value = true;
    }

    function closeModalCheckin() {
        modalCheckin.value = false;
        selectedAlunaId.value = "";
        errors.value = {};
    }

    function formatarDataHora(dataHora) {
        if (!dataHora) return "-";

        return new Intl.DateTimeFormat("pt-BR", {
            dateStyle: "short",
            timeStyle: "short",
        }).format(new Date(dataHora));
    }
</script>

<template>
    <MainLayout>
        <Loading
            v-if="pageLoading"
            class="absolute right-1/2 top-1/2 -translate-1/2"
        />

        <template v-else>
            <nav class="list-toolbar">
                <Button
                    v-if="isAdmin"
                    bg="var(--color-success)"
                    color="black"
                    class="hover:scale-102"
                    @click="modalCheckin = true"
                >
                    Registrar check-in
                </Button>

                <Button
                    v-if="isAluna"
                    bg="var(--color-success)"
                    color="black"
                    class="hover:scale-102"
                    @click="registrarMeuCheckin"
                >
                    <Loading v-if="checkinLoading" :size="24" />
                    <template v-else>Registrar meu check-in</template>
                </Button>
                <SearchInput
                    :model="pesquisa"
                    @update-value="pesquisa = $event"
                    placeholder="Pesquisar frequências"
                />
            </nav>

            <Errors :error="errors['geral']" />

            <p v-if="successMessage" class="success-message">
                {{ successMessage }}
            </p>

            <Transition name="modal" mode="out-in">
                <Modal v-if="modalCheckin" @close="closeModalCheckin">
                    <form @submit.prevent="registrarCheckinAdmin">
                        <h1 class="text-center font-bold text-2xl mb-5">
                            Registrar Check-in
                        </h1>

                        <Errors :error="errors['geral']" />

                        <div class="flex gap-4 mb-5">
                            <Select
                                :model="selectedAlunaId"
                                @update-value="selectedAlunaId = $event"
                                label="Aluna"
                                :error="errors['aluna']"
                            >
                                <option value="">Selecione uma aluna</option>

                                <option
                                    v-for="a in alunas"
                                    :key="a.id"
                                    :value="a.id"
                                >
                                    {{ a.nome }} - {{ a.cpf }}
                                </option>
                            </Select>
                        </div>

                        <Button
                            type="submit"
                            variant="success"
                            class="font-bold active:scale-98"
                        >
                            <Loading v-if="checkinLoading" :size="24" />
                            <template v-else>Registrar Check-in</template>
                        </Button>
                    </form>
                </Modal>
            </Transition>

            <section
                v-if="frequenciasFiltradas.length"
                class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10 place-items-center"
            >
                <Card v-for="f in frequenciasFiltradas" :key="f.id">
                    <template #header>
                        <h3>{{ f.aluna?.nome || "Aluna" }}</h3>
                        <p class="id">#{{ f.id }}</p>
                    </template>

                    <div class="card-group flex flex-col">
                        <label>Data e hora:</label>
                        <span class="flex items-center gap-2">
                            <Clock3 :size="18" />
                            {{ formatarDataHora(f.dataHoraEntrada) }}
                        </span>
                    </div>

                    <template #footer>
                        <div class="buttons mt-2 flex gap-3">
                            <Button
                                v-if="isAdmin"
                                @click="openModalConfirmRemoveFrequencia(f)"
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
            </section>

            <p v-else class="empty-state">
                Nenhuma frequência registrada.
            </p>

            <Transition name="modal" mode="out-in">
                <Confirm
                    v-if="modalConfirmRemoveFrequencia"
                    @close="modalConfirmRemoveFrequencia = false"
                    @confirm="sendDeleteRequest(selectedFrequenciaToDelete.id)"
                    :loading="deleteLoading"
                    title="Exclusão de Frequência"
                    message="Deseja mesmo excluir esta frequência?"
                />
            </Transition>
        </template>
    </MainLayout>
</template>

<style scoped>
    .success-message {
        margin-bottom: 16px;
        color: var(--color-success);
        font-weight: 600;
    }

    .empty-state {
        margin-top: 40px;
        color: #b6bac5;
        text-align: center;
    }

    label {
        color: #b6bac5;
        font-size: 0.9rem;
    }

    .id {
        color: #b6bac5;
        font-size: 0.9rem;
    }

    .list-toolbar {
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
    }

    .list-toolbar > :first-child {
        max-width: 240px;
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
