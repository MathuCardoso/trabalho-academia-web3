package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Matricula;
import br.edu.ifpr.academia.enums.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findByStatus(StatusMatricula status);

    List<Matricula> findByStatusAndDataVencimentoBetween(
            StatusMatricula status,
            LocalDate dataInicio,
            LocalDate dataFim
    );

    List<Matricula> findByAlunaId(Long alunaId);

    List<Matricula> findByTreinoProfessoraId(Long professoraId);

    List<Matricula> findByStatusAndDataVencimentoBefore(
            StatusMatricula status,
            LocalDate data
    );

    boolean existsByAlunaIdAndStatusAndDataVencimentoGreaterThanEqual(
            Long alunaId,
            StatusMatricula status,
            LocalDate data
    );
}