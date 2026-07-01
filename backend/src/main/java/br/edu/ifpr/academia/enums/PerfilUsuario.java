package br.edu.ifpr.academia.enums;

/*
 * Enum que define o perfil de acesso do usuario no sistema.
 *
 * ADMIN:
 * Acessa o painel administrativo e pode gerenciar tudo.
 *
 * PROFESSORA:
 * Acessa o painel da professora e pode trabalhar com treinos/alunas vinculadas.
 *
 * ALUNA:
 * Acessa o painel da aluna e pode ver seus próprios dados, treino,
 * matrícula e frequência.
 */
public enum PerfilUsuario {
    ADMIN,
    PROFESSORA,
    ALUNA
}