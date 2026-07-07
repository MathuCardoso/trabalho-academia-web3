import { useApi } from "@/composables/useApi";

const api = useApi();

export async function getFrequencia(id) {
    return await api.get(`api/frequencias/${id}`);
}

export async function getFrequencias() {
    return await api.get("api/frequencias");
}

export async function getFrequenciasPorAluna(alunaId) {
    return await api.get(`api/frequencias/aluna/${alunaId}`);
}

export async function postFrequencia(fields) {
    return await api.post("api/frequencias", fields);
}

export async function putFrequencia(fields) {
    return await api.put(`api/frequencias/${fields.id}`, fields);
}

export async function deleteFrequencia(id) {
    return await api.remove(`api/frequencias/${id}`);
}

export async function registrarCheckin(alunaId) {
    return await api.post(`api/frequencias/checkin/${alunaId}`);
}