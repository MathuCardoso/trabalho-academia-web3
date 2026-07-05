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
    import { computed, onMounted, provide, ref } from "vue";
    import Confirm from "@/components/modal/Confirm.vue";
    import Loading from "@/components/icons/Loading.vue";
    import Select from "@/components/form/Select.vue";
    import Errors from "@/components/form/Errors.vue";
    import { useAuthStore } from "@/stores/authStore";

    provide("headerTitle", "Listagem de Professoras");

    const auth = useAuthStore();
    const canManage = computed(() => auth.usuario?.perfil === "ADMIN");

    const pageLoading = ref(true);
    const submitLoading = ref(false);
    const deleteLoading = ref(false);

    const modalAddProfessora = ref(false);
    const modalConfirmRemoveProfessora = ref(false);
    const selectedProfessoraToDelete = ref(null);

    function professoraVazia() {
        return {
            id: "",
            nome: "",
            email: "",
            cref: "",
            especialidade: "",
            senhaInicial: "",
            status: "ATIVO",
        };
    }

    const Professora = ref(professoraVazia());

    async function prepareUpdate(id) {
        try {
            Professora.value = (await getProfessora(id)).data;
            Professora.value.senhaInicial = "";
            modalAddProfessora.value = true;
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        }
    }

    const errors = ref({});

    async function createOrUpdate() {
        submitLoading.value = true;

        try {
            if (Professora.value.id) {
                const payload = { ...Professora.value };
                delete payload.senhaInicial;
                await putProfessora(payload);
            } else {
                await postProfessora(Professora.value);
            }
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
            submitLoading.value = false;
            return;
        }

        closeModalAddProfessora();

        await carregarProfessoras();
    }

    async function sendDeleteRequest(id) {
        deleteLoading.value = true;
        try {
            await deleteProfessora(id);
            await carregarProfessoras();
            modalConfirmRemoveProfessora.value = false;
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
            modalConfirmRemoveProfessora.value = false;
        } finally {
            deleteLoading.value = false;
        }
    }

    function closeModalAddProfessora() {
        modalAddProfessora.value = false;
        Professora.value = professoraVazia();
        errors.value = {};
        submitLoading.value = false;
    }

    function openModalConfirmRemoveProfessora(professora) {
        selectedProfessoraToDelete.value = professora;
        modalConfirmRemoveProfessora.value = true;
    }

    const professoras = ref([]);
    async function carregarProfessoras() {
        professoras.value = (await getProfessoras()).data;
    }

    onMounted(async () => {
        try {
            await carregarProfessoras();
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        } finally {
            pageLoading.value = false;
        }
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
                v-if="canManage"
                bg="var(--color-success)"
                color="black"
                class="hover:scale-102"
                @click="modalAddProfessora = true"
            >
                Adicionar professora
            </Button>
        </nav>
        <Errors :error="errors['geral']" />
        <Transition name="modal" mode="out-in">
            <Modal v-if="modalAddProfessora" @close="closeModalAddProfessora">
                <form @submit.prevent="createOrUpdate()">
                    <h1 class="text-center font-bold text-2xl mb-5">
                        Adicionar Professora
                    </h1>
                    <Errors :error="errors['geral']" />

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
                            :model="Professora.cref"
                            @update-value="Professora.cref = $event"
                            label="CREF"
                            placeholder="Insira o CREF"
                            :error="errors['cref']"
                        />
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

                    <div v-if="!Professora.id" class="flex gap-4 mb-5">
                        <Input
                            :model="Professora.senhaInicial"
                            @update-value="Professora.senhaInicial = $event"
                            label="Senha inicial"
                            type="password"
                            placeholder="Insira a senha inicial"
                            :error="errors['senhaInicial']"
                        />
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
                    <label>CREF:</label>
                    <span>{{ p.cref }}</span>
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
                            v-if="canManage"
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
                            v-if="canManage"
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
