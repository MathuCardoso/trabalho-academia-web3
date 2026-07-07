import { API_URL } from "@/config/api";
import router from "@/router";
import { useAuthStore } from "@/stores/authStore";

export function useApi() {
    async function request(uri, options = {}) {
        let response;

        try {
            response = await fetch(buildUrl(uri), {
                ...options,
                headers: {
                    ...getHeaders(),
                    ...(options.headers || {}),
                },
            });
        } catch (error) {
            throw {
                success: false,
                message: "Nao foi possivel conectar ao servidor.",
                errors: { geral: "Nao foi possivel conectar ao servidor." },
                data: null,
                status: 0,
            };
        }

        let data = null;
        try {
            data = await response.json();
        } catch (error) {
            data = null;
        }

        if (response.status == 401) {
            const auth = useAuthStore();
            auth.logout();
            router.push("/login");
        }

        return {
            ok: response.ok,
            status: response.status,
            data: data,
        };
    }

    function buildUrl(uri) {
        const baseUrl = API_URL.replace(/\/+$/, "");
        const path = uri.replace(/^\/+/, "");

        return `${baseUrl}/${path}`;
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

    function normalizeErrors(data) {
        if (!data) return {};

        if (
            data.errors &&
            typeof data.errors === "object" &&
            Object.keys(data.errors).length
        ) {
            return data.errors;
        }

        if (
            data.erros &&
            typeof data.erros === "object" &&
            Object.keys(data.erros).length
        ) {
            return data.erros;
        }

        if (data.mensagem) {
            return { geral: data.mensagem };
        }

        if (data.message) {
            return { geral: data.message };
        }

        if (typeof data === "object") {
            return data;
        }

        return {};
    }

    function normalizeMessage(data, fallback) {
        return data?.mensagem || data?.message || fallback;
    }

    function errorResponse(response, fallback) {
        return {
            success: false,
            message: normalizeMessage(response.data, fallback),
            errors: normalizeErrors(response.data),
            data: response.data,
            status: response.status,
        };
    }

    async function get(uri) {
        const response = await request(uri, { method: "GET" });
        if (!response.ok)
            throw errorResponse(response, "Erro ao buscar dados.");

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
            body: JSON.stringify(fields || {}),
        });
        if (!response.ok) throw errorResponse(response, "Erro ao criar registro.");

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
            body: JSON.stringify(fields || {}),
        });
        if (!response.ok)
            throw errorResponse(response, "Erro ao atualizar registro");

        return {
            success: true,
            message: "Registro atualizado com sucesso",
            data: response.data,
            status: response.status,
        };
    }

    async function patch(uri, fields) {
        const response = await request(uri, {
            method: "PATCH",
            ...(fields ? { body: JSON.stringify(fields) } : {}),
        });
        if (!response.ok)
            throw errorResponse(response, "Erro ao alterar registro");

        return {
            success: true,
            message: "Registro alterado com sucesso",
            data: response.data,
            status: response.status,
        };
    }

    async function remove(uri) {
        const response = await request(uri, { method: "DELETE" });
        if (!response.ok)
            throw errorResponse(response, "Erro ao remover registro");

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
        patch,
        remove,
    };
}
