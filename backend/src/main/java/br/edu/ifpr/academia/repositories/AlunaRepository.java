package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.enums.StatusCadastro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlunaRepository extends JpaRepository<Aluna, Long> {

    /*
     * Busca alunas pelo status.
     * Exemplo: ATIVO ou INATIVO.
     */
    List<Aluna> findByStatus(StatusCadastro status);
}