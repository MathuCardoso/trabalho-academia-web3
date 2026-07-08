package br.edu.ifpr.academia.dtos;

import jakarta.validation.constraints.NotBlank;

/*
 * DTO usado para receber os dados de login.
 *
 * O login pode ser:
 * - admin para ADMIN
 * - CPF para ALUNA
 * - CREF para PROFESSORA
 */
public class LoginRequest {

    @NotBlank(message = "O login é obrigatorio")
    private String login;

    @NotBlank(message = "A senha é obrigatoria")
    private String senha;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}