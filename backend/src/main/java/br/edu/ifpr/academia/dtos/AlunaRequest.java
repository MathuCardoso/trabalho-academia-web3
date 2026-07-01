package br.edu.ifpr.academia.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

/*
 * DTO usado para cadastrar uma aluna.
 *
 * A senha inicial vem aqui porque sera usada para criar o Usuario.
 * A entidade Aluna nao deve ter senha.
 */
@Getter
@Setter
public class AlunaRequest {

    @NotBlank(message = "O nome e obrigatorio")
    @Size(max = 100, message = "O nome deve conter até 100 caracteres")
    private String nome;

    @NotBlank(message = "O e-mail e obrigatorio")
    @Email(message = "Informe um e-mail valido")
    @Size(max = 150, message = "O email deve conter no máximo 150 caracteres")
    private String email;

    @NotBlank(message = "O telefone e obrigatorio")
    @CPF(message = "CPF inválido")
    private String telefone;

    @NotBlank(message = "O CPF e obrigatorio")
    @Size(min = 14, max = 14, message = "O CPF deve conter 14 caracteres.")
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatoria")
    @Past(message = "A data de nascimento deve ser uma data passada")
    private LocalDate dataNascimento;

    @NotBlank(message = "A senha inicial e obrigatoria")
    @Size(min = 8, max = 100)
    private String senhaInicial;
}