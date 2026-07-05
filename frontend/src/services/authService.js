import { useApi } from "@/composables/useApi";

const api = useApi();

export async function login(login, senha) {
    return await api.post("api/auth/login", {
        login: login,
        senha: senha,
    });
}
