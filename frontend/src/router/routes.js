import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from '@/views/auth/RegisterView.vue';
import HomeView from '@/views/HomeView.vue';
import ProfessorsListView from '@/views/ProfessorsListView.vue';
import StudentsListView from '@/views/StudentsListView.vue';
import TrainingListView from '@/views/TrainingListView.vue';

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
        component: StudentsListView,
        meta: {
            title: "Alunas",
        },
    },
    {
        path: "/professoras",
        name: "professorasList",
        component: ProfessorsListView,
        meta: {
            title: "Professoras",
        },
    },
    {
        path: "/treinos",
        name: "trainingList",
        component: TrainingListView,
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
