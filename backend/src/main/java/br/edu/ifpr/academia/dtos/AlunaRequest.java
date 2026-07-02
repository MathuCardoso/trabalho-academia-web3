package br.edu.ifpr.academia.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public class AlunaRequest {

    @NotBlank(message = "O nome e obrigatorio")
    private String nome;

    @NotBlank(message = "O e-mail e obrigatorio")
    @Email(message = "Informe um e-mail valido")
    private String email;

    @NotBlank(message = "O telefone e obrigatorio")
    private String telefone;

    @NotBlank(message = "O CPF e obrigatorio")
    @Size(min = 14, max = 14, message = "O CPF deve conter 14 caracteres.")
    @CPF(message = "CPF invalido")
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatoria")
    private LocalDate dataNascimento;

    @NotBlank(message = "A senha inicial e obrigatoria")
    @Size(min = 8, max = 100)
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
    
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSenhaInicial() {
        return senhaInicial;
    }

    public void setSenhaInicial(String senhaInicial) {
        this.senhaInicial = senhaInicial;
    }
}