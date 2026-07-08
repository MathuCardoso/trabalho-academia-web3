package br.edu.ifpr.academia.entities;

import br.edu.ifpr.academia.enums.PerfilUsuario;
import br.edu.ifpr.academia.enums.StatusCadastro;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @NotBlank(message = "O login é obrigatorio")
    @Column(nullable = false, unique = true)
    private String login;

    /*
     * Senha criptografada.
     *
     * Nunca salvar senha pura aqui.
     * Nunca retornar esse campo no JSON de login.
     */
    @NotBlank(message = "A senha é obrigatoria")
    @Column(nullable = false)
    @JsonIgnore
    private String senha;

    /*
     * Perfil de acesso:
     * ADMIN, PROFESSORA ou ALUNA.
     */
    @NotNull(message = "O perfil é obrigatorio")
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
     * O vinculo com Aluna ou Professora fica nas entidades de cadastro.
     *
     * Assim, Usuario permanece focado apenas no acesso ao sistema.
     */
}
