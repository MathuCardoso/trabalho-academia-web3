import { API_URL } from "@/config/api";
import router from "@/router";
import { useAuthStore } from "@/stores/authStore";

export function useApi() {
    async function request(uri, options) {
        const response = await fetch(API_URL + uri, {
            ...options,
            headers: getHeaders(),
        });

        if (response.status == 401 || response.status == 403) {
            const auth = useAuthStore();
            auth.logout();
            router.push("/login");
        }

        let data = null;
        try {
            data = await response.json();
        } catch (error) {
            console.log("No Content Returned");
        }

        return {
            ok: response.ok,
            status: response.status,
            data: data,
        };
    }

    function getHeaders() {
        const headers = {
            "Content-Type": "application/json",
        };

        const token = localStorage.getItem("token");

        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }

        return headers;
    }

    async function get(uri) {
        const response = await request(uri, { method: "GET" });
        if (!response.ok)
            return {
                success: false,
                message: "Erro ao buscar dados.",
                status: response.status,
            };
        console.log(response);
        return {
            success: true,
            message: "Dados buscados com sucesso",
            data: response.data,
            status: response.status,
        };
    }

    async function post(uri, fields) {
        const response = await request(uri, {
            method: "POST",
            body: JSON.stringify(fields),
        });
        if (!response.ok)
            throw {
                success: false,
                message: "Erro ao criar registro.",
                errors: response.data,
                status: response.status,
            };

        return {
            success: true,
            message: "Registro criado com sucesso.",
            data: response.data,
            status: response.status,
        };
    }

    async function put(uri, fields) {
        const response = await request(uri, {
            method: "PUT",
            body: JSON.stringify(fields),
        });
        if (!response.ok)
            throw {
                success: false,
                message: "Erro ao atualizar registro",
                data: response.data,
                status: response.status,
            };

        return {
            success: true,
            message: "Registro atualizado com sucesso",
            data: response.data,
            status: response.status,
        };
    }

    async function remove(uri) {
        const response = await request(uri, { method: "DELETE" });
        if (!response.ok)
            throw {
                success: false,
                message: "Erro ao remover registro",
                status: response.status,
            };
        return {
            success: true,
            message: "Registro removido com sucesso",
            status: response.status,
        };
    }

    return {
        get,
        post,
        put,
        remove,
    };
}
