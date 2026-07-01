package br.edu.ifpr.academia.entities;

import br.edu.ifpr.academia.enums.StatusCadastro;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * Entidade com os dados profissionais da professora.
 *
 * A Professora NAO tem senha.
 * A Professora NAO tem isAdmin.
 * O acesso dela fica na entidade Usuario.
 */
@Entity
@Data
public class Professora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome e obrigatorio")
    private String nome;

    @NotBlank(message = "O e-mail e obrigatorio")
    @Email(message = "Informe um e-mail valido")
    @Column(unique = true)
    private String email;

    /*
     * CREF unico da professora.
     *
     * O CREF sera usado como login do Usuario da professora.
     */
    @NotBlank(message = "O CREF e obrigatorio")
    @Column(unique = true)
    private String cref;

    @NotBlank(message = "A especialidade e obrigatoria")
    private String especialidade;

    @Enumerated(EnumType.STRING)
    private StatusCadastro status = StatusCadastro.ATIVO;
}