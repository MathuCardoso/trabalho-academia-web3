package br.edu.ifpr.academia.entities;

import br.edu.ifpr.academia.enums.PerfilUsuario;
import br.edu.ifpr.academia.enums.StatusCadastro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
 * Entidade responsavel pelo acesso ao sistema.
 *
 * A senha fica aqui, e nao em Aluna nem em Professora.
 *
 * Login:
 * - ADMIN usa um login proprio, exemplo: admin
 * - ALUNA usa CPF
 * - PROFESSORA usa CREF
 */
@Entity
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Login unico do usuario.
     *
     * Para aluna: CPF
     * Para professora: CREF
     * Para admin: login proprio
     */
    @NotBlank(message = "O login e obrigatorio")
    @Column(nullable = false, unique = true)
    private String login;

    /*
     * Senha criptografada.
     *
     * Nunca salvar senha pura aqui.
     * Nunca retornar esse campo no JSON de login.
     */
    @NotBlank(message = "A senha e obrigatoria")
    @Column(nullable = false)
    private String senha;

    /*
     * Perfil de acesso:
     * ADMIN, PROFESSORA ou ALUNA.
     */
    @NotNull(message = "O perfil e obrigatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil;

    /*
     * Status do usuario.
     * Usuario INATIVO nao deve conseguir fazer login.
     */
    @Enumerated(EnumType.STRING)
    private StatusCadastro status = StatusCadastro.ATIVO;

    /*
     * Vinculo opcional com Aluna.
     *
     * Preenchido apenas quando perfil = ALUNA.
     * Usuario ADMIN e PROFESSORA deixam isso null.
     */
    @OneToOne
    @JoinColumn(name = "aluna_id", unique = true)
    private Aluna aluna;

    /*
     * Vinculo opcional com Professora.
     *
     * Preenchido apenas quando perfil = PROFESSORA.
     * Usuario ADMIN e ALUNA deixam isso null.
     */
    @OneToOne
    @JoinColumn(name = "professora_id", unique = true)
    private Professora professora;
}