package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.enums.StatusCadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessoraRepository extends JpaRepository<Professora, Long> {

    /*
     * Busca professoras pelo status.
     * Exemplo: ATIVO ou INATIVO.
     */
    List<Professora> findByStatus(StatusCadastro status);
}