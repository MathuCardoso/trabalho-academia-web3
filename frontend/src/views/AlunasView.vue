<script setup>
    import Button from "@/components/form/Button.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import Card from "@/components/ui/Card.vue";
    import { getAlunas } from "@/services/alunasService";
    import { Pencil, Trash2 } from "@lucide/vue";
    import { onMounted, provide } from "vue";
    
    provide("headerTitle", "Listagem de Alunas");

    onMounted(async () => {
        const alunas = await getAlunas();
    });
</script>

<template>
    <MainLayout>
        <nav class="mb-4 grid grid-cols-6 place-items-center">
            <button class="bg-success p-2 rounded grid">Adicionar aluna</button>
        </nav>
        <section
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10 place-items-center"
        >
            <Card v-for="n in 10">
                <template #header>
                    <h3>Matheus Cardoso</h3>
                    <p class="id">#{{ n }}</p>
                </template>
                <div class="card-group flex flex-col">
                    <label>Email:</label>
                    <span>mathcardoso792@gmail.com</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>CPF:</label>
                    <span>124.121.939-75</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Telefone:</label>
                    <span>45 99928-6562</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Data de Nascimento:</label>
                    <span>20/11/2005</span>
                </div>
                <div class="card-group flex flex-col">
                    <label>Status:</label>
                    <span>Ativo</span>
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
        </section>
    </MainLayout>
</template>

<style scoped></style>
