<script setup>
    import Button from "@/components/form/Button.vue";
    import Errors from "@/components/form/Errors.vue";
    import Loading from "@/components/icons/Loading.vue";
    import MainLayout from "@/components/layout/MainLayout.vue";
    import { getAlunas } from "@/services/alunaService";
    import {
        getFrequencias,
        getFrequenciasPorAluna,
        registrarCheckin,
    } from "@/services/frequenciaService";
    import {
        getMatriculas,
        getMatriculasPorAluna,
        getMatriculasPorProfessora,
    } from "@/services/matriculaService";
    import { getProfessoras } from "@/services/professoraService";
    import {
        getTreinos,
        getTreinosPorProfessora,
    } from "@/services/treinoService";
    import { useAuthStore } from "@/stores/authStore";
    import {
        Activity,
        BadgeCheck,
        CalendarClock,
        CircleAlert,
        ClipboardCheck,
        Dumbbell,
        UserRoundCheck,
        UsersRound,
    } from "@lucide/vue";
    import { computed, onMounted, provide, ref } from "vue";
    import { RouterLink } from "vue-router";

    const auth = useAuthStore();
    const isAdmin = computed(() => auth.usuario?.perfil === "ADMIN");
    const isAluna = computed(() => auth.usuario?.perfil === "ALUNA");
    const isProfessora = computed(() => auth.usuario?.perfil === "PROFESSORA");
    const loading = ref(true);
    const checkinLoading = ref(false);
    const error = ref("");
    const successMessage = ref("");

    const dados = ref({
        alunas: [],
        professoras: [],
        treinos: [],
        matriculas: [],
        frequencias: [],
    });

    provide("headerTitle", "Olá, " + auth.usuario.nome);
    provide("headerDescription", "Bem-vindo ao sistema de gestão de academia");

    const matriculaAtual = computed(() =>
        dados.value.matriculas.find((matricula) => matricula.status === "ATIVA")
    );

    const cards = computed(() => {
        if (isAluna.value) return cardsDaAluna();
        if (isProfessora.value) return cardsDaProfessora();
        return cardsDoAdmin();
    });

    function cardsDoAdmin() {
        const hoje = inicioDoDia(new Date());
        const limite = new Date(hoje);
        limite.setDate(limite.getDate() + 7);

        return [
            card(
                "Alunas cadastradas",
                dados.value.alunas.length,
                `${contarStatus(dados.value.alunas, "ATIVO")} ativas`,
                UsersRound,
                "pink",
                "/alunas"
            ),
            card(
                "Professoras",
                dados.value.professoras.length,
                `${contarStatus(dados.value.professoras, "ATIVO")} ativas`,
                Dumbbell,
                "cyan",
                "/professoras"
            ),
            card(
                "Treinos",
                dados.value.treinos.length,
                `${contarStatus(dados.value.treinos, "ATIVO")} ativos`,
                ClipboardCheck,
                "green",
                "/treinos"
            ),
            card(
                "Matrículas",
                dados.value.matriculas.length,
                `${contarStatus(dados.value.matriculas, "ATIVA")} ativas`,
                BadgeCheck,
                "blue",
                "/matriculas"
            ),
            card(
                "Matrículas vencidas",
                dados.value.matriculas.filter(
                    (matricula) => matricula.status === "VENCIDA"
                ).length,
                "Requerem atenção",
                CircleAlert,
                "red",
                "/matriculas"
            ),
            card(
                "Vencem em 7 dias",
                dados.value.matriculas.filter((matricula) => {
                    const vencimento = dataLocal(matricula.dataVencimento);
                    return (
                        matricula.status === "ATIVA" &&
                        vencimento >= hoje &&
                        vencimento <= limite
                    );
                }).length,
                "Matrículas ativas",
                CalendarClock,
                "yellow",
                "/matriculas"
            ),
        ];
    }

    function cardsDaAluna() {
        const matricula = matriculaAtual.value;
        const ultimaFrequencia = dados.value.frequencias.at(-1);

        return [
            card(
                "Matrícula atual",
                matricula?.status || "Sem matrícula",
                matricula?.treino?.nome || "Nenhum treino vinculado",
                BadgeCheck,
                "pink"
            ),
            card(
                "Vencimento",
                formatarData(matricula?.dataVencimento),
                matricula ? "Matrícula ativa" : "Sem vencimento",
                CalendarClock,
                "yellow"
            ),
            card(
                "Treino atual",
                matricula?.treino?.nome || "Não definido",
                detalhesDoTreino(matricula?.treino),
                Dumbbell,
                "cyan",
                null,
                true
            ),
            card(
                "Check-ins",
                dados.value.frequencias.length,
                ultimaFrequencia
                    ? `Último em ${formatarDataHora(ultimaFrequencia.dataHoraEntrada)}`
                    : "Nenhuma frequência registrada",
                Activity,
                "green"
            ),
        ];
    }

    function cardsDaProfessora() {
        const alunasVinculadas = new Set(
            dados.value.matriculas
                .map((matricula) => matricula.aluna?.id)
                .filter(Boolean)
        );
        const treinosAtivos = contarStatus(dados.value.treinos, "ATIVO");
        const matriculasAtivas = contarStatus(
            dados.value.matriculas,
            "ATIVA"
        );

        return [
            card(
                "Meus treinos",
                dados.value.treinos.length,
                `${treinosAtivos} ativos`,
                ClipboardCheck,
                "pink",
                "/treinos"
            ),
            card(
                "Alunas vinculadas",
                alunasVinculadas.size,
                "Nos seus treinos",
                UsersRound,
                "cyan"
            ),
            card(
                "Matrículas ativas",
                matriculasAtivas,
                `${dados.value.matriculas.length} no total`,
                UserRoundCheck,
                "green"
            ),
            card(
                "Treinos inativos",
                dados.value.treinos.length - treinosAtivos,
                "Disponíveis para revisão",
                CircleAlert,
                "yellow",
                "/treinos"
            ),
        ];
    }

    function card(titulo, valor, detalhe, icone, cor, destino = null, compact = false) {
        return { titulo, valor, detalhe, icone, cor, destino, compact };
    }

    function contarStatus(registros, status) {
        return registros.filter((registro) => registro.status === status).length;
    }

    function inicioDoDia(data) {
        data.setHours(0, 0, 0, 0);
        return data;
    }

    function dataLocal(data) {
        return data ? new Date(`${data}T00:00:00`) : new Date(0);
    }

    function formatarData(data) {
        if (!data) return "Não disponível";
        return new Intl.DateTimeFormat("pt-BR").format(dataLocal(data));
    }

    function formatarDataHora(data) {
        if (!data) return "-";
        return new Intl.DateTimeFormat("pt-BR", {
            dateStyle: "short",
            timeStyle: "short",
        }).format(new Date(data));
    }

    function detalhesDoTreino(treino) {
        if (!treino) return "Nenhum treino vinculado";

        const nivel = treino.nivel || "Nível não informado";
        const descricao = treino.descricao || "Descrição não informada";
        return `${nivel}. ${descricao}`;
    }

    async function registrarMinhaFrequencia() {
        checkinLoading.value = true;
        error.value = "";
        successMessage.value = "";

        try {
            await registrarCheckin(auth.usuario.alunaId);
            successMessage.value = "Frequência registrada com sucesso.";
            dados.value.frequencias = (
                await getFrequenciasPorAluna(auth.usuario.alunaId)
            ).data;
        } catch (requestError) {
            error.value =
                requestError.errors?.geral || requestError.message;
        } finally {
            checkinLoading.value = false;
        }
    }

    onMounted(async () => {
        try {
            if (isAdmin.value) {
                const [alunas, professoras, treinos, matriculas, frequencias] =
                    await Promise.all([
                        getAlunas(),
                        getProfessoras(),
                        getTreinos(),
                        getMatriculas(),
                        getFrequencias(),
                    ]);
                dados.value = {
                    alunas: alunas.data,
                    professoras: professoras.data,
                    treinos: treinos.data,
                    matriculas: matriculas.data,
                    frequencias: frequencias.data,
                };
            } else if (isAluna.value) {
                const [matriculas, frequencias] = await Promise.all([
                    getMatriculasPorAluna(auth.usuario.alunaId),
                    getFrequenciasPorAluna(auth.usuario.alunaId),
                ]);
                dados.value.matriculas = matriculas.data;
                dados.value.frequencias = frequencias.data;
            } else {
                const [treinos, matriculas] = await Promise.all([
                    getTreinosPorProfessora(auth.usuario.professoraId),
                    getMatriculasPorProfessora(auth.usuario.professoraId),
                ]);
                dados.value.treinos = treinos.data;
                dados.value.matriculas = matriculas.data;
            }
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
        <section aria-labelledby="dashboard-title">
            <div class="dashboard-heading">
                <h2 id="dashboard-title">Visão geral</h2>
                <Button
                    v-if="isAluna"
                    variant="success"
                    class="checkin-button"
                    :disabled="checkinLoading"
                    @click="registrarMinhaFrequencia"
                >
                    <Loading v-if="checkinLoading" :size="21" />
                    <template v-else>Registrar frequência</template>
                </Button>
            </div>

            <Errors v-if="error" :error="error" />
            <p v-if="successMessage" class="success-message">
                {{ successMessage }}
            </p>
            <Loading v-if="loading" class="dashboard-loading" />

            <div v-else class="stats-grid">
                <component
                    :is="card.destino ? RouterLink : 'article'"
                    v-for="card in cards"
                    :key="card.titulo"
                    v-bind="card.destino ? { to: card.destino } : {}"
                    class="stat-card"
                    :class="`stat-card--${card.cor}`"
                >
                    <div class="stat-card__header">
                        <component :is="card.icone" :size="22" />
                        <span>{{ card.titulo }}</span>
                    </div>
                    <strong :class="{ compact: card.compact }">
                        {{ card.valor }}
                    </strong>
                    <span class="stat-card__detail">{{ card.detalhe }}</span>
                </component>
            </div>
        </section>
    </MainLayout>
</template>

<style scoped>
    .dashboard-heading {
        margin-bottom: 24px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
    }

    .dashboard-heading h2 {
        font-size: 1.35rem;
        font-weight: 700;
    }

    .checkin-button {
        width: 220px;
        min-height: 44px;
        flex: 0 0 220px;
    }

    .success-message {
        margin-bottom: 16px;
        color: var(--color-success);
        font-weight: 600;
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

    .stat-card__header span,
    .stat-card strong {
        overflow-wrap: anywhere;
    }

    .stat-card strong {
        font-size: 2.25rem;
        line-height: 1.1;
    }

    .stat-card strong.compact {
        font-size: 1.35rem;
    }

    .stat-card__detail {
        color: #b6bac5;
        font-size: 0.9rem;
        line-height: 1.45;
    }

    .stat-card--pink { --card-accent: var(--color-pink-accent); }
    .stat-card--cyan { --card-accent: var(--color-cyan-accent); }
    .stat-card--green { --card-accent: #4ade80; }
    .stat-card--blue { --card-accent: #60a5fa; }
    .stat-card--red { --card-accent: #f87171; }
    .stat-card--yellow { --card-accent: #facc15; }

    @media (max-width: 1024px) {
        .stats-grid {
            grid-template-columns: repeat(2, minmax(0, 1fr));
        }
    }

    @media (max-width: 640px) {
        .dashboard-heading {
            align-items: stretch;
            flex-direction: column;
        }

        .checkin-button {
            width: 100%;
            flex-basis: 44px;
        }

        .stats-grid {
            grid-template-columns: 1fr;
        }
    }
</style>
