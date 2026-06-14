package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Matricula;
import br.edu.ifpr.academia.enums.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    /*
     * Busca matriculas pelo status.
     * Exemplo: ATIVA, VENCIDA ou CANCELADA.
     */
    List<Matricula> findByStatus(StatusMatricula status);

    /*
     * Busca matriculas por status e por intervalo de vencimento.
     * Sera usado para listar apenas matriculas ATIVAS que estao proximas do vencimento.
     */
    List<Matricula> findByStatusAndDataVencimentoBetween(
            StatusMatricula status,
            LocalDate dataInicio,
            LocalDate dataFim
    );
}