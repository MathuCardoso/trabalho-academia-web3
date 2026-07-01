package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.enums.StatusCadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Repository da entidade Aluna.
 */
@Repository
public interface AlunaRepository extends JpaRepository<Aluna, Long> {

    /*
     * Busca alunas pelo status.
     * Exemplo: ATIVO ou INATIVO.
     */
    List<Aluna> findByStatus(StatusCadastro status);

    /*
     * Busca uma aluna pelo CPF.
     *
     * O CPF tambem sera usado como login do usuario da aluna.
     */
    Optional<Aluna> findByCpf(String cpf);

    /*
     * Verifica se ja existe uma aluna com esse CPF.
     *
     * Usado para impedir cadastro duplicado.
     */
    boolean existsByCpf(String cpf);

    /*
     * Verifica se ja existe uma aluna com esse e-mail.
     *
     * Usado para impedir cadastro duplicado.
     */
    boolean existsByEmail(String email);
}