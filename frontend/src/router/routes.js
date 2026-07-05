import AlunasView from "@/views/AlunasView.vue";
import LoginView from "@/views/auth/LoginView.vue";
import HomeView from "@/views/HomeView.vue";
import MatriculasView from "@/views/MatriculasView.vue";
import ProfessorasView from "@/views/ProfessorasView.vue";
import TreinosView from "@/views/TreinosView.vue";

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
            roles: ["ADMIN", "PROFESSORA"],
        },
    },
    {
        path: "/professoras",
        name: "professorasList",
        component: ProfessorasView,
        meta: {
            title: "Professoras",
            requiresAuth: true,
            roles: ["ADMIN", "PROFESSORA", "ALUNA"],
        },
    },
    {
        path: "/treinos",
        name: "treinosList",
        component: TreinosView,
        meta: {
            title: "Treinos",
            requiresAuth: true,
            roles: ["ADMIN", "PROFESSORA", "ALUNA"],
        },
    },
    {
        path: "/matriculas",
        name: "matriculasList",
        component: MatriculasView,
        meta: {
            title: "Matrículas",
            requiresAuth: true,
            roles: ["ADMIN", "PROFESSORA", "ALUNA"],
        },
    },
];
