import { API_URL } from "@/config/api";

export function useApi() {
    async function get(uri) {
        try {
            const response = await fetch(API_URL + uri, {
                method: "GET",
                headers: "content-type: application/json",
            });
            if (!response.ok) throw new Error("Erro inesperado.");

            return await response.json();
        } catch (error) {
            return error;
        }
    }

    async function post(uri, fields) {
        try {
            const response = await fetch(API_URL + uri, {
                method: "POST",
                headers: { "content-type": "application/json" },
                body: JSON.stringify({ ...fields }),
            });
            const data = response.json();
            if (!response.ok) throw data;

            return data;
        } catch (error) {
            return error;
        }
    }

    async function put(uri) {}

    async function remove(uri) {}

    return {
        get,
        post,
        put,
        remove,
    };
}
