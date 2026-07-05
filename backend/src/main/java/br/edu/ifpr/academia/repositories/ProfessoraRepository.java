package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.enums.StatusCadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * Repository da entidade Professora.
 */
@Repository
public interface ProfessoraRepository extends JpaRepository<Professora, Long> {

    /*
     * Busca professoras pelo status.
     * Exemplo: ATIVO ou INATIVO.
     */
    List<Professora> findByStatus(StatusCadastro status);

    /*
     * Busca uma professora pelo CREF.
     *
     * O CREF tambem sera usado como login do usuario da professora.
     */
    Optional<Professora> findByCref(String cref);

    /*
     * Busca professora pelo Usuario vinculado.
     */
    Optional<Professora> findByUsuario_Id(Long usuarioId);

    /*
     * Verifica se ja existe uma professora com esse CREF.
     *
     * Usado para impedir cadastro duplicado.
     */
    boolean existsByCref(String cref);

    /*
     * Verifica CREF duplicado ignorando a propria professora.
     */
    boolean existsByCrefAndIdNot(String cref, Long id);

    /*
     * Verifica se ja existe uma professora com esse e-mail.
     *
     * Usado para impedir cadastro duplicado.
     */
    boolean existsByEmail(String email);

    /*
     * Verifica e-mail duplicado ignorando a propria professora.
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}
