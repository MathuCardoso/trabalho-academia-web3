package br.edu.ifpr.academia.entities;

import br.edu.ifpr.academia.enums.StatusCadastro;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Aluna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O Nome é obrigatório")
    private String nome;

    @NotBlank(message = "O E-mail é obrigatório")
    @Email(message = "Informe um E-mail válido")
    private String email;

    @NotBlank(message = "A Senha é obrigatória")
    @Min(value = 6, message = "A senha deve conter 6 caracteres ou mais")
    @Max(value = 64, message = "A senha deve conter até 64 caracteres")
    private String senha;

    @NotBlank(message = "O Telefone é obrigatório")
    private String telefone;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @NotNull(message = "A Data de Nascimento é obrigatória")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private StatusCadastro status = StatusCadastro.ATIVO;
}
