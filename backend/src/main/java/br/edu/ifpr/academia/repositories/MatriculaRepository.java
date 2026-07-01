package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Matricula;
import br.edu.ifpr.academia.enums.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/*
 * Repository da entidade Matricula.
 */
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    /*
     * Busca matriculas pelo status.
     * Exemplo: ATIVA, VENCIDA ou CANCELADA.
     */
    List<Matricula> findByStatus(StatusMatricula status);

    /*
     * Busca matriculas por status e por intervalo de vencimento.
     *
     * Usado para listar apenas matriculas ATIVAS
     * que estao proximas do vencimento.
     */
    List<Matricula> findByStatusAndDataVencimentoBetween(
            StatusMatricula status,
            LocalDate dataInicio,
            LocalDate dataFim
    );

    /*
     * Busca todas as matriculas de uma aluna.
     *
     * Usado para o painel da aluna.
     */
    List<Matricula> findByAlunaId(Long alunaId);

    /*
     * Verifica se uma aluna possui matricula com determinado status.
     *
     * Usado para validar check-in/frequencia.
     */
    boolean existsByAlunaIdAndStatus(Long alunaId, StatusMatricula status);

    /*
     * Busca matriculas de alunas vinculadas aos treinos de uma professora.
     *
     * Pode ser usado no painel da professora.
     */
    List<Matricula> findByTreinoProfessoraId(Long professoraId);
}