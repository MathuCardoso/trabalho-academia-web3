import AlunasView from "@/views/AlunasView.vue";
import LoginView from "@/views/auth/LoginView.vue";
import HomeView from "@/views/HomeView.vue";
import MatriculasView from "@/views/MatriculasView.vue";
import ProfessorasView from "@/views/ProfessorasView.vue";
import TreinosView from "@/views/TreinosView.vue";
import FrequenciasView from "@/views/FrequenciasView.vue";

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
];
