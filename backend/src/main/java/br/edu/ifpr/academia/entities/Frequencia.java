package br.edu.ifpr.academia.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Frequencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluna_id")
    private Aluna aluna;

    @NotNull(message = "A data e hora de entrada são obrigatórias")
    private LocalDateTime dataHoraEntrada;
}
