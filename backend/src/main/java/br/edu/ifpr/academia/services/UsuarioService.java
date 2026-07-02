package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.entities.Usuario;
import br.edu.ifpr.academia.enums.PerfilUsuario;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Service responsavel pelas regras de negocio da entidade Usuario.
 *
 * Usuario representa o acesso ao sistema:
 * - login
 * - senha criptografada
 * - perfil
 * - status de acesso
 * - vinculo opcional com Aluna ou Professora
 *
 */
@Service
public class UsuarioService {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Lista todos os usuarios.
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /*
     * Busca usuario pelo ID.
     */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
    }

    /*
     * Busca usuario pelo login.
     *
     * Usado no processo de login.
     */
    public Usuario buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));
    }

    /*
     * Cria usuario ADMIN.
     *
     * O ADMIN nao precisa estar vinculado a Aluna nem Professora.
     */
    public Usuario criarAdmin(String login, String senha) {
        if (usuarioRepository.existsByLogin(login)) {
            throw new RuntimeException("Login ja cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setSenha(this.passwordEncoder.encode(senha));
        usuario.setPerfil(PerfilUsuario.ADMIN);
        usuario.setStatus(StatusCadastro.ATIVO);

        return usuarioRepository.save(usuario);
    }

    /*
     * Cria usuario para Aluna.
     *
     * Regra:
     * - login = CPF da aluna
     * - perfil = ALUNA
     * - status = ATIVO
     * - senha criptografada
     */
    public Usuario criarUsuarioAluna(Aluna aluna, String senhaInicial) {
        String login = aluna.getCpf();

        if (usuarioRepository.existsByLogin(login)) {
            throw new RuntimeException("Ja existe usuario cadastrado com este CPF");
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(senhaInicial));
        usuario.setPerfil(PerfilUsuario.ALUNA);
        usuario.setStatus(StatusCadastro.ATIVO);
        usuario.setAluna(aluna);

        return usuarioRepository.save(usuario);
    }

    /*
     * Cria usuario para Professora.
     *
     * Regra:
     * - login = CREF da professora
     * - perfil = PROFESSORA
     * - status = ATIVO
     * - senha criptografada
     */
    public Usuario criarUsuarioProfessora(Professora professora, String senhaInicial) {
        String login = professora.getCref();

        if (usuarioRepository.existsByLogin(login)) {
            throw new RuntimeException("Ja existe usuario cadastrado com este CREF");
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(senhaInicial));
        usuario.setPerfil(PerfilUsuario.PROFESSORA);
        usuario.setStatus(StatusCadastro.ATIVO);
        usuario.setProfessora(professora);

        return usuarioRepository.save(usuario);
    }

    /*
     * Ativa um usuario pelo ID do proprio Usuario.
     */
    public Usuario ativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setStatus(StatusCadastro.ATIVO);
        return usuarioRepository.save(usuario);
    }

    /*
     * Inativa um usuario pelo ID do proprio Usuario.
     *
     * Usuario INATIVO nao consegue fazer login.
     */
    public Usuario inativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setStatus(StatusCadastro.INATIVO);
        return usuarioRepository.save(usuario);
    }

    /*
     * Ativa o Usuario vinculado a uma Aluna.
     *
     * Usado quando o cadastro da Aluna for ativado.
     *
     * Assim:
     * - Aluna.status = ATIVO
     * - Usuario.status = ATIVO
     */
    public Usuario ativarUsuarioDaAluna(Long alunaId) {
        Usuario usuario = usuarioRepository.findByAlunaId(alunaId)
                .orElseThrow(() -> new RuntimeException("Usuario da aluna nao encontrado"));

        usuario.setStatus(StatusCadastro.ATIVO);
        return usuarioRepository.save(usuario);
    }

    /*
     * Inativa o Usuario vinculado a uma Aluna.
     *
     * Usado quando o cadastro da Aluna for inativado.
     *
     * Assim:
     * - Aluna.status = INATIVO
     * - Usuario.status = INATIVO
     *
     * Dessa forma, a aluna tambem deixa de conseguir fazer login.
     */
    public Usuario inativarUsuarioDaAluna(Long alunaId) {
        Usuario usuario = usuarioRepository.findByAlunaId(alunaId)
                .orElseThrow(() -> new RuntimeException("Usuario da aluna nao encontrado"));

        usuario.setStatus(StatusCadastro.INATIVO);
        return usuarioRepository.save(usuario);
    }

    /*
     * Ativa o Usuario vinculado a uma Professora.
     *
     * Usado quando o cadastro da Professora for ativado.
     *
     * Assim:
     * - Professora.status = ATIVO
     * - Usuario.status = ATIVO
     */
    public Usuario ativarUsuarioDaProfessora(Long professoraId) {
        Usuario usuario = usuarioRepository.findByProfessoraId(professoraId)
                .orElseThrow(() -> new RuntimeException("Usuario da professora nao encontrado"));

        usuario.setStatus(StatusCadastro.ATIVO);
        return usuarioRepository.save(usuario);
    }

    /*
     * Inativa o Usuario vinculado a uma Professora.
     *
     * Usado quando o cadastro da Professora for inativado.
     *
     * Assim:
     * - Professora.status = INATIVO
     * - Usuario.status = INATIVO
     *
     * Dessa forma, a professora tambem deixa de conseguir fazer login.
     */
    public Usuario inativarUsuarioDaProfessora(Long professoraId) {
        Usuario usuario = usuarioRepository.findByProfessoraId(professoraId)
                .orElseThrow(() -> new RuntimeException("Usuario da professora nao encontrado"));

        usuario.setStatus(StatusCadastro.INATIVO);
        return usuarioRepository.save(usuario);
    }

    /*
     * Exclui usuario.
     */
    public void excluir(Long id) {
        usuarioRepository.deleteById(id);
    }
}