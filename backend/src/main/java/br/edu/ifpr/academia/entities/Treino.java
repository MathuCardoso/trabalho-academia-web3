package br.edu.ifpr.academia.entities;

import br.edu.ifpr.academia.enums.NivelTreino;
import br.edu.ifpr.academia.enums.StatusCadastro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/*
 * Entidade que representa um treino.
 *
 * Cada treino pertence a uma professora.
 * A aluna nao se liga diretamente ao treino.
 * A ligacao entre aluna e treino acontece pela Matricula.
 */
@Entity
@Data
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do treino e obrigatorio")
    private String nome;

    @NotBlank(message = "A descricao do treino e obrigatoria")
    private String descricao;

    @NotNull(message = "O nivel do treino e obrigatorio")
    @Enumerated(EnumType.STRING)
    private NivelTreino nivel;

    /*
     * Status do treino.
     * Permite inativar treino sem excluir do banco.
     */
    @Enumerated(EnumType.STRING)
    private StatusCadastro status = StatusCadastro.ATIVO;

    /*
     * Professora responsavel pelo treino.
     */
    @NotNull(message = "A professora e obrigatoria")
    @ManyToOne
    @JoinColumn(name = "professora_id")
    private Professora professora;
}