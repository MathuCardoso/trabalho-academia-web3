import { useApi } from "@/composables/useApi";
const api = useApi();

export async function getAluna(id) {
    return await api.get(`api/alunas/${id}`);
}
export async function getAlunas() {
    return await api.get("api/alunas");
}

export async function postAluna(fields) {
    return await api.post("api/alunas", fields);
}

export async function putAluna(fields) {
    return await api.put(`api/alunas/${fields.id}`, fields);
}

export async function putMeuPerfilAluna(id, fields) {
    return await api.put(`api/alunas/${id}/perfil`, fields);
}

export async function ativarAluna(id) {
    return await api.patch(`api/alunas/${id}/ativar`);
}

export async function inativarAluna(id) {
    return await api.patch(`api/alunas/${id}/inativar`);
}

export async function deleteAluna(id) {
    return await api.remove(`api/alunas/${id}`);
}
