package br.edu.ifpr.academia.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.edu.ifpr.academia.enums.StatusCadastro;

import java.time.LocalDate;

public class AlunaRequest {

    @NotBlank(message = "O nome é obrigatorio")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatorio")
    @Email(message = "Informe um e-mail valido")
    private String email;

    @NotBlank(message = "O telefone é obrigatorio")
    private String telefone;

    @NotBlank(message = "O CPF é obrigatorio")
    @Size(min = 14, max = 14, message = "O CPF deve conter 14 caracteres.")
    @CPF(message = "CPF invalido")
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatoria")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;

    @NotNull(message = "O status é obrigatoria")
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
}