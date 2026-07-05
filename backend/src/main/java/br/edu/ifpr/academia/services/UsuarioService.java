package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.entities.Usuario;
import br.edu.ifpr.academia.enums.PerfilUsuario;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.exceptions.ApiException;
import br.edu.ifpr.academia.repositories.AlunaRepository;
import br.edu.ifpr.academia.repositories.ProfessoraRepository;
import br.edu.ifpr.academia.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
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
 *
 */
@Service
public class UsuarioService {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final AlunaRepository alunaRepository;
    private final ProfessoraRepository professoraRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AlunaRepository alunaRepository,
            ProfessoraRepository professoraRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.alunaRepository = alunaRepository;
        this.professoraRepository = professoraRepository;
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
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    }

    /*
     * Busca usuario pelo login.
     *
     * Usado no processo de login.
     */
    public Usuario buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
    }

    /*
     * Busca a Aluna dona de um Usuario.
     */
    public Aluna buscarAlunaPorUsuario(Long usuarioId) {
        return alunaRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Aluna do usuario nao encontrada"));
    }

    /*
     * Busca a Professora dona de um Usuario.
     */
    public Professora buscarProfessoraPorUsuario(Long usuarioId) {
        return professoraRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Professora do usuario nao encontrada"));
    }

    /*
     * Cria usuario ADMIN.
     *
     * O ADMIN nao precisa estar vinculado a Aluna nem Professora.
     */
    public Usuario criarAdmin(String login, String senha) {
        if (usuarioRepository.existsByLogin(login)) {
            throw new ApiException(HttpStatus.CONFLICT, "Login ja cadastrado", "login");
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
            throw new ApiException(HttpStatus.CONFLICT, "Ja existe usuario cadastrado com este CPF", "cpf");
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(senhaInicial));
        usuario.setPerfil(PerfilUsuario.ALUNA);
        usuario.setStatus(statusOuAtivo(aluna.getStatus()));

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
            throw new ApiException(HttpStatus.CONFLICT, "Ja existe usuario cadastrado com este CREF", "cref");
        }

        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setSenha(passwordEncoder.encode(senhaInicial));
        usuario.setPerfil(PerfilUsuario.PROFESSORA);
        usuario.setStatus(statusOuAtivo(professora.getStatus()));

        return usuarioRepository.save(usuario);
    }

    /*
     * Atualiza o login do Usuario quando CPF/CREF do cadastro muda.
     */
    public Usuario atualizarLogin(Usuario usuario, String novoLogin) {
        if (usuario == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Usuario vinculado nao encontrado");
        }

        if (usuarioRepository.existsByLoginAndIdNot(novoLogin, usuario.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Login ja cadastrado", "login");
        }

        usuario.setLogin(novoLogin);
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
        Aluna aluna = alunaRepository.findById(alunaId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Aluna nao encontrada"));

        Usuario usuario = aluna.getUsuario();

        if (usuario == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Usuario da aluna nao encontrado");
        }

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
        Aluna aluna = alunaRepository.findById(alunaId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Aluna nao encontrada"));

        Usuario usuario = aluna.getUsuario();

        if (usuario == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Usuario da aluna nao encontrado");
        }

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
        Professora professora = professoraRepository.findById(professoraId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Professora nao encontrada"));

        Usuario usuario = professora.getUsuario();

        if (usuario == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Usuario da professora nao encontrado");
        }

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
        Professora professora = professoraRepository.findById(professoraId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Professora nao encontrada"));

        Usuario usuario = professora.getUsuario();

        if (usuario == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Usuario da professora nao encontrado");
        }

        usuario.setStatus(StatusCadastro.INATIVO);
        return usuarioRepository.save(usuario);
    }

    /*
     * Exclui usuario.
     */
    public void excluir(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    private StatusCadastro statusOuAtivo(StatusCadastro status) {
        return status == null ? StatusCadastro.ATIVO : status;
    }
}
