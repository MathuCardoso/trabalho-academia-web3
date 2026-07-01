package br.edu.ifpr.academia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/*
 * Entidade que registra a presenca/check-in da aluna.
 */
@Entity
@Data
public class Frequencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Aluna que registrou a frequencia.
     */
    @NotNull(message = "A aluna e obrigatoria")
    @ManyToOne
    @JoinColumn(name = "aluna_id")
    private Aluna aluna;

    /*
     * Data e hora do check-in.
     *
     * Pode ser preenchida automaticamente no service.
     */
    private LocalDateTime dataHoraEntrada;
}