function normalizar(valor) {
    return String(valor ?? "")
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();
}

export function filtrarPorTermo(registros, termo) {
    const pesquisa = normalizar(termo).trim();
    if (!pesquisa) return registros;

    return registros.filter((registro) =>
        normalizar(JSON.stringify(registro)).includes(pesquisa)
    );
}
