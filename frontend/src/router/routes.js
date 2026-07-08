import AlunasView from "@/views/AlunasView.vue";
import LoginView from "@/views/auth/LoginView.vue";
import HomeView from "@/views/HomeView.vue";
import MatriculasView from "@/views/MatriculasView.vue";
import ProfessorasView from "@/views/ProfessorasView.vue";
import TreinosView from "@/views/TreinosView.vue";
import FrequenciasView from "@/views/FrequenciasView.vue";
import ErrorView from "@/views/ErrorView.vue";

export default [
    {
        path: "/",
        name: "home",
        component: HomeView,
        meta: {
            title: "Home",
            requiresAuth: true,
        },
    },
    {
        path: "/login",
        name: "login",
        component: LoginView,
        meta: {
            title: "Login",
        },
    },
    {
        path: "/alunas",
        name: "alunasList",
        component: AlunasView,
        meta: {
            title: "Alunas",
            requiresAuth: true,
            roles: ["ADMIN"],
        },
    },
    {
        path: "/professoras",
        name: "professorasList",
        component: ProfessorasView,
        meta: {
            title: "Professoras",
            requiresAuth: true,
            roles: ["ADMIN"],
        },
    },
    {
        path: "/treinos",
        name: "treinosList",
        component: TreinosView,
        meta: {
            title: "Treinos",
            requiresAuth: true,
            roles: ["ADMIN", "PROFESSORA"],
        },
    },
    {
        path: "/matriculas",
        name: "matriculasList",
        component: MatriculasView,
        meta: {
            title: "Matrículas",
            requiresAuth: true,
            roles: ["ADMIN"],
        },
    },
    {
        path: "/frequencias",
        name: "frequenciasList",
        component: FrequenciasView,
        meta: {
            title: "Frequência",
            requiresAuth: true,
            roles: ["ADMIN", "ALUNA"],
        },
    },
    {
        path: "/403",
        name: "forbidden",
        component: ErrorView,
        props: {
            code: 403,
            title: "Acesso negado",
            message: "Você não tem permissão para acessar esta página.",
        },
        meta: {
            title: "Acesso negado",
        },
    },
    {
        path: "/404",
        name: "notFound",
        component: ErrorView,
        props: {
            code: 404,
            title: "Página não encontrada",
            message: "A página que você está tentando acessar não existe ou foi removida.",
        },
        meta: {
            title: "Página não encontrada",
        },
    },
    {
        path: "/500",
        name: "serverError",
        component: ErrorView,
        props: {
            code: 500,
            title: "Erro interno do servidor",
            message: "Ocorreu um erro inesperado no sistema. Tente novamente mais tarde.",
        },
        meta: {
            title: "Erro interno",
        },
    },
    {
        path: "/:pathMatch(.*)*",
        redirect: "/404",
    },
];
