package br.edu.ifpr.academia.entities;

import br.edu.ifpr.academia.enums.StatusMatricula;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluna_id")
    @NotNull(message = "A aluna é obrigatória")
    private Aluna aluna;

    @ManyToOne
    @JoinColumn(name = "treino_id")
    @NotNull(message = "O treino é obrigatório")
    private Treino treino;

    @NotNull(message = "A data de início é obrigatória")
    private LocalDate dataInicio;

    @NotNull(message = "A data de vencimento é obrigatória")
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    private StatusMatricula status = StatusMatricula.ATIVA;
}