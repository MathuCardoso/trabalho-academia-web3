import { useApi } from "@/composables/useApi";
const api = useApi();

export async function getProfessora(id) {
    return await api.get(`api/professoras/${id}`);
}
export async function getProfessoras() {
    return await api.get("api/professoras");
}

export async function postProfessora(fields) {
    return await api.post("api/professoras", fields);
}

export async function putProfessora(fields) {
    return await api.put(`api/professoras/${fields.id}`, fields);
}

export async function putMeuPerfilProfessora(id, fields) {
    return await api.put(`api/professoras/${id}/perfil`, fields);
}

export async function ativarProfessora(id) {
    return await api.patch(`api/professoras/${id}/ativar`);
}

export async function inativarProfessora(id) {
    return await api.patch(`api/professoras/${id}/inativar`);
}

export async function deleteProfessora(id) {
    return await api.remove(`api/professoras/${id}`);
}
