package br.edu.ifpr.academia.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * DTO usado para cadastrar uma professora.
 *
 * A senha inicial vem aqui porque sera usada para criar o Usuario.
 * A entidade Professora nao deve ter senha.
 */
public class ProfessoraRequest {

    @NotBlank(message = "O nome e obrigatorio")
    private String nome;

    @NotBlank(message = "O e-mail e obrigatorio")
    @Email(message = "Informe um e-mail valido")
    private String email;

    @NotBlank(message = "O CREF e obrigatorio")
    private String cref;

    @NotBlank(message = "A especialidade e obrigatoria")
    private String especialidade;

    @NotBlank(message = "A senha inicial e obrigatoria")
    private String senhaInicial;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getCref() {
        return cref;
    }

    public void setCref(String cref) {
        this.cref = cref;
    }


    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }


    public String getSenhaInicial() {
        return senhaInicial;
    }

    public void setSenhaInicial(String senhaInicial) {
        this.senhaInicial = senhaInicial;
    }
}