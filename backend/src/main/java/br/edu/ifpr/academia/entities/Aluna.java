package br.edu.ifpr.academia.entities;

import br.edu.ifpr.academia.enums.StatusCadastro;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

/*
 * Entidade com os dados cadastrais da aluna.
 *
 * A Aluna NAO tem senha.
 * O acesso dela fica na entidade Usuario.
 */
@Entity
@Getter
@Setter
public class Aluna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome e obrigatorio")
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "O e-mail e obrigatorio")
    @Email(message = "Informe um e-mail valido")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "O telefone e obrigatorio")
    @Column(nullable = false, length = 15)
    private String telefone;

    @NotBlank(message = "O CPF e obrigatorio")
    @Size(min = 14, max = 14, message = "O CPF deve conter 14 caracteres.")
    @CPF(message = "CPF invalido")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;
    
    @NotNull(message = "A data de nascimento e obrigatoria")
    @Column(nullable = false)
    private LocalDate dataNascimento;

    @NotNull(message = "O status e obrigatorio")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusCadastro status = StatusCadastro.ATIVO;

    /*
     * Usuario de acesso da aluna.
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
