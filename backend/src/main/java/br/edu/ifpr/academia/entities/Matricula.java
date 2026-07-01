package br.edu.ifpr.academia.entities;

import br.edu.ifpr.academia.enums.StatusMatricula;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/*
 * Entidade que liga Aluna e Treino.
 *
 * Ela evita uma relacao direta N:N entre Aluna e Treino.
 */
@Entity
@Data
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Aluna vinculada a matricula.
     */
    @NotNull(message = "A aluna e obrigatoria")
    @ManyToOne
    @JoinColumn(name = "aluna_id")
    private Aluna aluna;

    /*
     * Treino vinculado a matricula.
     */
    @NotNull(message = "O treino e obrigatorio")
    @ManyToOne
    @JoinColumn(name = "treino_id")
    private Treino treino;

    @NotNull(message = "A data de inicio e obrigatoria")
    private LocalDate dataInicio;

    @NotNull(message = "A data de vencimento e obrigatoria")
    private LocalDate dataVencimento;

    /*
     * Status da matricula:
     * ATIVA, VENCIDA ou CANCELADA.
     */
    @Enumerated(EnumType.STRING)
    private StatusMatricula status = StatusMatricula.ATIVA;
}