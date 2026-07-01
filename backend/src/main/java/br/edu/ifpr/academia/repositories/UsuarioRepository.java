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
     * Busca o Usuario vinculado a uma Aluna.
     *
     * No nosso modelo atual, Usuario possui:
     *
     * @OneToOne
     * private Aluna aluna;
     *
     * Por isso conseguimos buscar pelo ID da aluna.
     */
    Optional<Usuario> findByAlunaId(Long alunaId);

    /*
     * Busca o Usuario vinculado a uma Professora.
     *
     * No nosso modelo atual, Usuario possui:
     *
     * @OneToOne
     * private Professora professora;
     *
     * Por isso conseguimos buscar pelo ID da professora.
     */
    Optional<Usuario> findByProfessoraId(Long professoraId);
}