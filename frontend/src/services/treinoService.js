import { useApi } from "@/composables/useApi";
const api = useApi();

export async function getTreino(id) {
    return await api.get(`api/treinos/${id}`);
}
export async function getTreinos() {
    return await api.get("api/treinos");
}

export async function getTreinosPorProfessora(professoraId) {
    return await api.get(`api/treinos/professora/${professoraId}`);
}

export async function postTreino(fields) {
    return await api.post("api/treinos", fields);
}

export async function putTreino(fields) {
    return await api.put(`api/treinos/${fields.id}`, fields);
}

export async function deleteTreino(id) {
    return await api.remove(`api/treinos/${id}`);
}
