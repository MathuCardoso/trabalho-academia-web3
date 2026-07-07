import { useApi } from "@/composables/useApi";
const api = useApi();

export async function getMatricula(id) {
    return await api.get(`api/matriculas/${id}`);
}
export async function getMatriculas() {
    return await api.get("api/matriculas");
}

export async function getMatriculasPorAluna(alunaId) {
    return await api.get(`api/matriculas/aluna/${alunaId}`);
}

export async function getMatriculasPorProfessora(professoraId) {
    return await api.get(`api/matriculas/professora/${professoraId}`);
}

export async function postMatricula(fields) {
    return await api.post("api/matriculas", fields);
}

export async function putMatricula(fields) {
    return await api.put(`api/matriculas/${fields.id}`, fields);
}

export async function deleteMatricula(id) {
    return await api.remove(`api/matriculas/${id}`);
}
