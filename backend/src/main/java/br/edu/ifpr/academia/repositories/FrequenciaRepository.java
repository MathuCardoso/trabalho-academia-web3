package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {

    /*
     * Busca todas as frequências de uma aluna pelo ID dela.
     */
    List<Frequencia> findByAlunaId(Long alunaId);

    /*
     * Busca frequências dentro de um intervalo de data e hora.
     */
    List<Frequencia> findByDataHoraEntradaBetween(LocalDateTime inicio, LocalDateTime fim);
}