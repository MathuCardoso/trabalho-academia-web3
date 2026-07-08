<script setup>
    import Button from "@/components/form/Button.vue";
    import {
        ArrowLeft,
        CircleAlert,
        House,
        SearchX,
        ServerCrash,
        ShieldX,
    } from "@lucide/vue";
    import { computed } from "vue";
    import { useRouter } from "vue-router";

    const props = defineProps({
        code: {
            type: [Number, String],
            default: 500,
        },
        title: {
            type: String,
            default: "Não foi possível carregar esta página",
        },
        message: {
            type: String,
            default: "Ocorreu um erro inesperado. Tente novamente mais tarde.",
        },
    });

    const router = useRouter();

    const errorIcon = computed(() => {
        const icons = {
            403: ShieldX,
            404: SearchX,
            500: ServerCrash,
        };

        return icons[String(props.code)] || CircleAlert;
    });

    function voltarAoInicio() {
        router.push("/");
    }

    function voltar() {
        if (window.history.state?.back) {
            router.back();
            return;
        }

        voltarAoInicio();
    }
</script>

<template>
    <main class="error-page">
        <section class="error-card" aria-labelledby="error-title">
            <div class="error-icon" aria-hidden="true">
                <component :is="errorIcon" :size="36" :stroke-width="1.8" />
            </div>

            <span class="error-label">Erro</span>
            <strong class="error-code">{{ code }}</strong>
            <h1 id="error-title">{{ title }}</h1>
            <p>{{ message }}</p>

            <div class="error-actions">
                <Button
                    class="error-button"
                    color="white"
                    bg="var(--color-pink-accent)"
                    @click="voltarAoInicio"
                >
                    Página inicial
                    <template #icon>
                        <House :size="19" />
                    </template>
                </Button>
                <Button
                    class="error-button error-button--secondary"
                    color="white"
                    bg="var(--color-text)"
                    @click="voltar"
                >
                    Voltar
                    <template #icon>
                        <ArrowLeft :size="19" />
                    </template>
                </Button>
            </div>
        </section>
    </main>
</template>

<style scoped>
    .error-page {
        min-height: 100vh;
        padding: 32px 20px;
        display: grid;
        place-items: center;
        background-color: var(--color-near-black);
    }

    .error-card {
        width: min(100%, 620px);
        padding: 48px;
        display: flex;
        align-items: center;
        flex-direction: column;
        text-align: center;
        border: 1px solid var(--color-text);
        border-top: 4px solid var(--color-pink-accent);
        border-radius: 8px;
        background-color: var(--color-card);
        box-shadow: 0 16px 40px #0000004d;
    }

    .error-icon {
        width: 64px;
        height: 64px;
        margin-bottom: 20px;
        display: grid;
        place-items: center;
        color: var(--color-cyan-accent);
        border: 1px solid var(--color-text);
        border-radius: 50%;
        background-color: var(--color-near-black);
    }

    .error-label {
        color: #b6bac5;
        font-size: 0.85rem;
        font-weight: 700;
        text-transform: uppercase;
    }

    .error-code {
        margin-top: 4px;
        color: var(--color-pink-accent);
        font-size: 4.5rem;
        line-height: 1;
    }

    h1 {
        margin-top: 18px;
        font-size: 1.75rem;
        font-weight: 700;
        overflow-wrap: anywhere;
    }

    p {
        max-width: 480px;
        margin-top: 12px;
        color: #b6bac5;
        line-height: 1.6;
        overflow-wrap: anywhere;
    }

    .error-actions {
        width: 100%;
        margin-top: 32px;
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 190px));
        justify-content: center;
        gap: 12px;
    }

    .error-button {
        width: 190px;
        min-height: 44px;
        gap: 9px;
        font-weight: 600;
    }

    .error-button--secondary {
        border: 1px solid #454955;
    }

    @media (max-width: 640px) {
        .error-card {
            padding: 36px 20px;
        }

        .error-code {
            font-size: 3.75rem;
        }

        h1 {
            font-size: 1.5rem;
        }

        .error-actions {
            grid-template-columns: 1fr;
        }

        .error-button {
            width: 100%;
        }
    }
</style>
