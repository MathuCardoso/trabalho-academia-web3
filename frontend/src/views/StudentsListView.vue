<script setup>
    import MainLayout from "@/components/layout/MainLayout.vue";
    import { provide } from "vue";
    provide("headerTitle", "Listagem de Alunas");
</script>

<template>
    <MainLayout>
        <nav class="mb-4 grid grid-cols-6 place-items-center">
            <button class="bg-green-light p-2 rounded grid">Adicionar aluna</button>
        </nav>
        <section class="grid grid-cols-3 gap-10 place-items-center">
            <article v-for="n in 10">
                <div class="card-header">
                    <h3>Matheus Cardoso</h3>
                    <p class="id">#{{ n }}</p>
                </div>
                <hr class="text-pink-accent my-3" />
                <div class="card-body">
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
                </div>
            </article>
        </section>
    </MainLayout>
</template>

<style scoped>
    article {
        position: relative;
        width: fit-content;
        height: fit-content;
        padding: 20px;
        background: linear-gradient(120deg, #2a2d3683, #0b0d12d3);
        box-shadow: 2px 2px 8px #0b0d12d3;
        border-radius: 18px;
        transition: all 0.3s ease;
        z-index: 0;
        &:hover {
            transform: translateY(-5px);
            box-shadow: 2px 2px 8px var(--color-pink-accent-shadow);
            z-index: 0;
        }

        .card-header {
            text-align: center;
            font-size: 1.2rem;

            .id {
                text-align: center;
                font-size: 0.9rem;
                color: rgb(163, 163, 163);
            }
        }

        .card-body {
            display: flex;
            flex-direction: column;
            gap: 10px;

            .card-group {
                label {
                    color: rgb(143, 143, 143);
                    font-weight: bold;
                }
                span {
                    color: rgb(211, 211, 211);
                }
            }
        }
    }
</style>
