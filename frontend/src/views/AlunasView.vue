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
    import { computed, onMounted, provide, ref } from "vue";
    import Confirm from "@/components/modal/Confirm.vue";
    import Loading from "@/components/icons/Loading.vue";
    import Select from "@/components/form/Select.vue";
    import Errors from "@/components/form/Errors.vue";
    import { useAuthStore } from "@/stores/authStore";

    provide("headerTitle", "Listagem de Alunas");

    const auth = useAuthStore();
    const canManage = computed(() => auth.usuario?.perfil === "ADMIN");

    const pageLoading = ref(true);
    const submitLoading = ref(false);
    const deleteLoading = ref(false);

    const modalAddAluna = ref(false);
    const modalConfirmRemoveAluna = ref(false);
    const selectedAlunaToDelete = ref(null);

    function alunaVazia() {
        return {
            id: "",
            nome: "",
            email: "",
            telefone: "",
            dataNascimento: "",
            cpf: "",
            senhaInicial: "",
            status: "ATIVO",
        };
    }

    const Aluna = ref(alunaVazia());

    async function prepareUpdate(id) {
        try {
            Aluna.value = (await getAluna(id)).data;
            Aluna.value.senhaInicial = "";
            modalAddAluna.value = true;
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
        }
    }

    const errors = ref({});

    async function createOrUpdate() {
        let response = null;
        submitLoading.value = true;
        try {
            if (Aluna.value.id) {
                const payload = { ...Aluna.value };
                delete payload.senhaInicial;
                response = await putAluna(payload);
            } else {
                response = await postAluna(Aluna.value);
            }
        } catch (e) {
            errors.value = e.errors || { geral: e.message };
            submitLoading.value = false;
            return;
        }
        closeModalAddAluna();

        await carregarAlunas();
    }

    async function sendDeleteRequest(id) {
        deleteLoading.value = true;
        try {
            await deleteAluna(id);
            await carregarAlunas();
            modalConfirmRemoveAluna.value = false;
        } catch (error) {
            errors.value = error.errors || { geral: error.message };
            modalConfirmRemoveAluna.value = false;
        } finally {
            deleteLoading.value = false;
        }
    }

    function closeModalAddAluna() {
        modalAddAluna.value = false;
        Aluna.value = alunaVazia();
        errors.value = {};
        submitLoading.value = false;
    }

    function openModalConfirmRemoveAluna(aluna) {
        selectedAlunaToDelete.value = aluna;
        modalConfirmRemoveAluna.value = true;
    }

    const alunas = ref([]);
    async function carregarAlunas() {
        alunas.value = (await getAlunas()).data;
    }

    onMounted(async () => {
        try {
            await carregarAlunas();
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
        <nav class="mb-4 grid grid-cols-6 place-items-center">
            <Button
                v-if="canManage"
                bg="var(--color-success)"
                color="black"
                class="hover:scale-102"
                @click="modalAddAluna = true"
            >
                Adicionar aluna
            </Button>
        </nav>
        <Errors :error="errors['geral']" />
        <Transition name="modal" mode="out-in">
            <Modal v-if="modalAddAluna" @close="closeModalAddAluna">
                <form @submit.prevent="createOrUpdate()">
                    <h1 class="text-center font-bold text-2xl mb-5">
                        Adicionar Aluna
                    </h1>
                    <Errors :error="errors['geral']" />

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
                            type="date"
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
                        <Input
                            v-if="!Aluna.id"
                            :model="Aluna.senhaInicial"
                            @update-value="Aluna.senhaInicial = $event"
                            label="Senha Inicial"
                            type="password"
                            :error="errors['senhaInicial']"
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
                            v-if="canManage"
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
                            v-if="canManage"
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
