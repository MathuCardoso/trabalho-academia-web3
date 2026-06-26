import { useApi } from "@/composables/useApi";

export async function getAlunas() {
    const api = useApi();
    return await api.get("api/alunas");
}

export async function postAluna(fields) {
    const api = useApi();
    return await api.post("api/alunas", fields);
}

export async function deleteAluna(id) {
    const api = useApi();
    return await api.remove(`api/alunas/${id}`);
}
