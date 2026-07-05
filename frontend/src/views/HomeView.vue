<script setup>
    import Errors from "@/components/form/Errors.vue";
    import Loading from "@/components/icons/Loading.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import { getAlunas } from "@/services/alunaService";
    import { getMatriculas } from "@/services/matriculaService";
    import { getProfessoras } from "@/services/professoraService";
    import { getTreinos } from "@/services/treinoService";
    import { useAuthStore } from "@/stores/authStore";
    import {
        BadgeCheck,
        CalendarClock,
        CircleAlert,
        ClipboardCheck,
        Dumbbell,
        UsersRound,
    } from "@lucide/vue";
    import { computed, onMounted, provide, ref } from "vue";

    const auth = useAuthStore();
    const isAdmin = computed(() => auth.usuario?.perfil === "ADMIN");
    const loading = ref(false);
    const error = ref("");

    const dados = ref({
        alunas: [],
        professoras: [],
        treinos: [],
        matriculas: [],
    });

    provide("headerTitle", "Olá, " + auth.usuario.nome);
    provide("headerDescription", "Bem-vindo ao sistema de gestão de academia");

    const cards = computed(() => {
        const alunasAtivas = dados.value.alunas.filter(
            (aluna) => aluna.status === "ATIVO"
        ).length;
        const professorasAtivas = dados.value.professoras.filter(
            (professora) => professora.status === "ATIVO"
        ).length;
        const treinosAtivos = dados.value.treinos.filter(
            (treino) => treino.status === "ATIVO"
        ).length;
        const matriculasAtivas = dados.value.matriculas.filter(
            (matricula) => matricula.status === "ATIVA"
        ).length;

        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);
        const limite = new Date(hoje);
        limite.setDate(limite.getDate() + 7);

        const matriculasVencidas = dados.value.matriculas.filter(
            (matricula) =>
                matricula.status === "VENCIDA" ||
                (matricula.status === "ATIVA" &&
                    dataDaMatricula(matricula) < hoje)
        ).length;
        const matriculasAVencer = dados.value.matriculas.filter((matricula) => {
            const vencimento = dataDaMatricula(matricula);
            return (
                matricula.status === "ATIVA" &&
                vencimento >= hoje &&
                vencimento <= limite
            );
        }).length;

        return [
            {
                titulo: "Alunas cadastradas",
                valor: dados.value.alunas.length,
                detalhe: `${alunasAtivas} ativas`,
                icone: UsersRound,
                destino: "/alunas",
                cor: "pink",
            },
            {
                titulo: "Professoras",
                valor: dados.value.professoras.length,
                detalhe: `${professorasAtivas} ativas`,
                icone: Dumbbell,
                destino: "/professoras",
                cor: "cyan",
            },
            {
                titulo: "Treinos",
                valor: dados.value.treinos.length,
                detalhe: `${treinosAtivos} ativos`,
                icone: ClipboardCheck,
                destino: "/treinos",
                cor: "green",
            },
            {
                titulo: "Matrículas",
                valor: dados.value.matriculas.length,
                detalhe: `${matriculasAtivas} ativas`,
                icone: BadgeCheck,
                destino: "/matriculas",
                cor: "blue",
            },
            {
                titulo: "Matrículas vencidas",
                valor: matriculasVencidas,
                detalhe: "Requerem atenção",
                icone: CircleAlert,
                destino: "/matriculas",
                cor: "red",
            },
            {
                titulo: "Vencem em 7 dias",
                valor: matriculasAVencer,
                detalhe: "Matrículas ativas",
                icone: CalendarClock,
                destino: "/matriculas",
                cor: "yellow",
            },
        ];
    });

    function dataDaMatricula(matricula) {
        return new Date(`${matricula.dataVencimento}T00:00:00`);
    }

    onMounted(async () => {
        if (!isAdmin.value) return;

        loading.value = true;
        try {
            const [alunas, professoras, treinos, matriculas] =
                await Promise.all([
                    getAlunas(),
                    getProfessoras(),
                    getTreinos(),
                    getMatriculas(),
                ]);

            dados.value = {
                alunas: alunas.data,
                professoras: professoras.data,
                treinos: treinos.data,
                matriculas: matriculas.data,
            };
        } catch (requestError) {
            error.value =
                requestError.errors?.geral || requestError.message;
        } finally {
            loading.value = false;
        }
    });
</script>

<template>
    <MainLayout>
        <section v-if="isAdmin" aria-labelledby="dashboard-title">
            <div class="dashboard-heading">
                <h2 id="dashboard-title">Visão geral</h2>
            </div>

            <Loading v-if="loading" class="dashboard-loading" />
            <Errors v-else-if="error" :error="error" />

            <div v-else class="stats-grid">
                <RouterLink
                    v-for="card in cards"
                    :key="card.titulo"
                    :to="card.destino"
                    class="stat-card"
                    :class="`stat-card--${card.cor}`"
                >
                    <div class="stat-card__header">
                        <component :is="card.icone" :size="22" />
                        <span>{{ card.titulo }}</span>
                    </div>
                    <strong>{{ card.valor }}</strong>
                    <span class="stat-card__detail">{{ card.detalhe }}</span>
                </RouterLink>
            </div>
        </section>
    </MainLayout>
</template>

<style scoped>
    .dashboard-heading {
        margin-bottom: 24px;
    }

    .dashboard-heading h2 {
        font-size: 1.35rem;
        font-weight: 700;
    }

    .dashboard-loading {
        margin: 80px auto;
    }

    .stats-grid {
        display: grid;
        grid-template-columns: repeat(3, minmax(0, 1fr));
        gap: 18px;
    }

    .stat-card {
        min-width: 0;
        min-height: 150px;
        padding: 20px;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        border: 1px solid var(--color-text);
        border-left: 4px solid var(--card-accent);
        border-radius: 8px;
        background-color: #1b1e27;
        transition:
            border-color 0.2s ease,
            transform 0.2s ease;
    }

    .stat-card:hover {
        border-color: var(--card-accent);
        transform: translateY(-2px);
    }

    .stat-card__header {
        min-width: 0;
        display: flex;
        align-items: center;
        gap: 10px;
        color: var(--card-accent);
        font-weight: 600;
    }

    .stat-card__header span {
        overflow-wrap: anywhere;
    }

    .stat-card strong {
        font-size: 2.25rem;
        line-height: 1;
    }

    .stat-card__detail {
        color: #b6bac5;
        font-size: 0.9rem;
    }

    .stat-card--pink {
        --card-accent: var(--color-pink-accent);
    }

    .stat-card--cyan {
        --card-accent: var(--color-cyan-accent);
    }

    .stat-card--green {
        --card-accent: #4ade80;
    }

    .stat-card--blue {
        --card-accent: #60a5fa;
    }

    .stat-card--red {
        --card-accent: #f87171;
    }

    .stat-card--yellow {
        --card-accent: #facc15;
    }

    @media (max-width: 1024px) {
        .stats-grid {
            grid-template-columns: repeat(2, minmax(0, 1fr));
        }
    }

    @media (max-width: 640px) {
        .stats-grid {
            grid-template-columns: 1fr;
        }
    }
</style>
