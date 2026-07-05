import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { login as loginUser } from "@/services/authService";

export const useAuthStore = defineStore("auth", () => {
    const usuario = ref(JSON.parse(localStorage.getItem("usuario")) || null);
    const token = ref(localStorage.getItem("token") || null);

    const isAuthenticated = computed(() => {
        return !!token.value;
    });

    async function login(login, senha) {
        const response = await loginUser(login, senha);
        const data = response.data;

        token.value = data.token;

        usuario.value = {
            id: data.usuarioId,
            usuarioId: data.usuarioId,
            login: data.login,
            nome: data.nome,
            perfil: data.perfil,
            status: data.status,
            alunaId: data.alunaId,
            professoraId: data.professoraId,
        };

        localStorage.setItem("token", token.value);
        localStorage.setItem("usuario", JSON.stringify(usuario.value));
    }

    function logout() {
        token.value = null;
        usuario.value = null;
        localStorage.removeItem("token");
        localStorage.removeItem("usuario");
    }

    return {
        token,
        usuario,
        login,
        logout,
        isAuthenticated,
    };
});
