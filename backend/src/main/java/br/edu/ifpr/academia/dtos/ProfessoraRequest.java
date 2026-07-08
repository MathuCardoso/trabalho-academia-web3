package br.edu.ifpr.academia.dtos;

import br.edu.ifpr.academia.enums.StatusCadastro;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    @NotBlank(message = "A senha e obrigatoria")
    @Size(min = 6, message =  "A senha deve conter pelo menos 6 caracteres.")
    private String senhaInicial;

    @NotNull(message = "O status e obrigatorio")
    private StatusCadastro status = StatusCadastro.ATIVO;

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

    public StatusCadastro getStatus() {
        return status;
    }

    public void setStatus(StatusCadastro status) {
        this.status = status;
    }
}
