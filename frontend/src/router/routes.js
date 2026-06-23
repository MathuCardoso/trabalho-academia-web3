import AlunasView from '@/views/AlunasView.vue';
import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from '@/views/auth/RegisterView.vue';
import HomeView from '@/views/HomeView.vue';
import MatriculasView from '@/views/MatriculasView.vue';
import ProfessorasView from '@/views/ProfessorasView.vue';
import TreinosView from '@/views/TreinosView.vue';

export default [

    {
        path: "/",
        name: "home",
        component: HomeView,
        meta: {
            title: "Home",
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
        },
    },
    {
        path: "/professoras",
        name: "professorasList",
        component: ProfessorasView,
        meta: {
            title: "Professoras",
        },
    },
    {
        path: "/treinos",
        name: "treinosList",
        component: TreinosView,
        meta: {
            title: "Treinos",
        },
    },
    {
        path: "/matriculas",
        name: "matriculasList",
        component: MatriculasView,
        meta: {
            title: "Treinos",
        },
    },
    {
        path: "/cadastro",
        name: "register",
        component: RegisterView,
        meta: {
            title: "Cadastro",
        },
    },
];
