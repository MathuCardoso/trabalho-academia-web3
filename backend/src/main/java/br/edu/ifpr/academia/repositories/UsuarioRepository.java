package br.edu.ifpr.academia.repositories;

import br.edu.ifpr.academia.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * Repository da entidade Usuario.
 *
 * Usuario representa o acesso ao sistema:
 * - login
 * - senha
 * - perfil
 * - status
 *
 * Aqui ficam as consultas relacionadas ao login
 * e tambem as buscas do Usuario vinculado a uma Aluna
 * ou Professora.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /*
     * Busca um usuario pelo login.
     *
     * O login pode ser:
     * - admin
     * - CPF da aluna
     * - CREF da professora
     */
    Optional<Usuario> findByLogin(String login);

    /*
     * Verifica se ja existe um usuario com determinado login.
     *
     * Usado para impedir login duplicado.
     */
    boolean existsByLogin(String login);

    /*
     * Verifica login duplicado ignorando o proprio Usuario.
     * Usado nas edicoes de CPF/CREF.
     */
    boolean existsByLoginAndIdNot(String login, Long id);
}
