package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Repository da entidade Frequencia.
 */
@Repository
public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {

    /*
     * Busca todas as frequencias de uma aluna pelo ID dela.
     */
    List<Frequencia> findByAlunaId(Long alunaId);

    /*
     * Busca frequencias dentro de um intervalo de data e hora.
     */
    List<Frequencia> findByDataHoraEntradaBetween(LocalDateTime inicio, LocalDateTime fim);
}