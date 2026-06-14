package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.enums.StatusCadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunaRepository extends JpaRepository<Aluna, Long> {

    /*
     * Busca alunas pelo status.
     * Exemplo: ATIVO ou INATIVO.
     */
    List<Aluna> findByStatus(StatusCadastro status);
}