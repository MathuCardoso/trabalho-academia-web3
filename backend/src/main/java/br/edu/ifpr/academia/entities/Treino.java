package br.edu.ifpr.academia.entities;

import br.edu.ifpr.academia.enums.NivelTreino;
import br.edu.ifpr.academia.enums.StatusCadastro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do treino é obrigatório")
    private String nome;

    @NotBlank(message = "A descrição do treino é obrigatória")
    private String descricao;

    @NotNull(message = "O nível do treino é obrigatório")
    @Enumerated(EnumType.STRING)
    private NivelTreino nivel;

    @ManyToOne
    @JoinColumn(name = "professora_id")
    @NotNull(message = "A professora é obrigatória")
    private Professora professora;

    @Enumerated(EnumType.STRING)
    private StatusCadastro status = StatusCadastro.ATIVO;
}