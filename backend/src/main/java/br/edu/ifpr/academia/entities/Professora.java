package br.edu.ifpr.academia.entities;

import br.edu.ifpr.academia.enums.StatusCadastro;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "O status e obrigatorio")
    @Enumerated(EnumType.STRING)
    private StatusCadastro status = StatusCadastro.ATIVO;

    /*
     * Usuario de acesso da professora.
     *
     * O campo completo nao e serializado para evitar expor senha.
     * No JSON, a API devolve apenas usuarioId.
     */
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @Transient
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }
}
