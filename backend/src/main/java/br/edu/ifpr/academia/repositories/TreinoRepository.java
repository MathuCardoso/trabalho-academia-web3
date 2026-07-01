package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.enums.StatusCadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Repository da entidade Treino.
 */
@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {

    /*
     * Busca treinos pelo status.
     * Exemplo: ATIVO ou INATIVO.
     */
    List<Treino> findByStatus(StatusCadastro status);

    /*
     * Busca todos os treinos de uma professora especifica.
     *
     * Usado para o painel da professora.
     */
    List<Treino> findByProfessoraId(Long professoraId);
}