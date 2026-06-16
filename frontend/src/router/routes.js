import LoginView from "@/views/auth/LoginView.vue";
import RegisterView from '@/views/auth/RegisterView.vue';
import HomeView from '@/views/HomeView.vue';

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
        path: "/cadastro",
        name: "register",
        component: RegisterView,
        meta: {
            title: "Cadastro",
        },
    },
];
