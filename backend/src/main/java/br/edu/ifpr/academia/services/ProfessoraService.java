package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.dtos.ProfessoraRequest;
import br.edu.ifpr.academia.dtos.AtualizarPerfilProfessoraRequest;
import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.entities.Usuario;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.exceptions.ApiException;
import br.edu.ifpr.academia.repositories.ProfessoraRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Service responsavel pelas regras de negocio de Professora.
 *
 * Professora guarda apenas dados profissionais.
 * Senha e login ficam em Usuario.
 */
@Service
public class ProfessoraService {

    private final ProfessoraRepository professoraRepository;
    private final UsuarioService usuarioService;

    public ProfessoraService(ProfessoraRepository professoraRepository, UsuarioService usuarioService) {
        this.professoraRepository = professoraRepository;
        this.usuarioService = usuarioService;
    }

    /*
     * Lista todas as professoras.
     */
    public List<Professora> listarTodas() {
        return professoraRepository.findAll();
    }

    /*
     * Busca professora pelo ID.
     */
    public Professora buscarPorId(Long id) {
        return professoraRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Professora nao encontrada"));
    }

    public boolean pertenceAoUsuario(Long professoraId, String login) {
        return professoraRepository.findById(professoraId)
                .map(Professora::getUsuario)
                .map(Usuario::getLogin)
                .filter(login::equals)
                .isPresent();
    }

    /*
     * Cadastra professora e cria usuario de acesso automaticamente.
     *
     * Regras:
     * - CREF unico
     * - e-mail unico
     * - cria Professora
     * - cria Usuario com login = CREF
     * - senha criptografada no Usuario
     * - perfil = PROFESSORA
     *
     * @Transactional garante que, se der erro ao criar o Usuario,
     * a Professora tambem nao fica salva sozinha no banco.
     */
    @Transactional
    public Professora cadastrarComUsuario(ProfessoraRequest request) {
        if (professoraRepository.existsByCref(request.getCref())) {
            throw new ApiException(HttpStatus.CONFLICT, "CREF ja cadastrado", "cref");
        }

        if (professoraRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "E-mail ja cadastrado", "email");
        }

        Professora professora = new Professora();
        professora.setNome(request.getNome());
        professora.setEmail(request.getEmail());
        professora.setCref(request.getCref());
        professora.setEspecialidade(request.getEspecialidade());
        professora.setStatus(statusOuAtivo(request.getStatus()));

        Usuario usuario = usuarioService.criarUsuarioProfessora(professora, request.getSenhaInicial());
        professora.setUsuario(usuario);

        return professoraRepository.save(professora);
    }

    /*
     * Salva professora diretamente.
     *
     * Mantido para usos internos ou testes.
     * Normalmente, para cadastro via API, usamos cadastrarComUsuario().
     */
    public Professora salvar(Professora professora) {
        return professoraRepository.save(professora);
    }

    /*
     * Atualiza dados profissionais da professora.
     *
     * Nao altera senha.
     * A senha pertence ao Usuario.
     */
    @Transactional
    public Professora atualizar(Long id, Professora dadosAtualizados) {
        Professora professora = buscarPorId(id);

        if (professoraRepository.existsByCrefAndIdNot(dadosAtualizados.getCref(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "CREF ja cadastrado", "cref");
        }

        if (professoraRepository.existsByEmailAndIdNot(dadosAtualizados.getEmail(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "E-mail ja cadastrado", "email");
        }

        professora.setNome(dadosAtualizados.getNome());
        professora.setEmail(dadosAtualizados.getEmail());
        professora.setCref(dadosAtualizados.getCref());
        professora.setEspecialidade(dadosAtualizados.getEspecialidade());
        professora.setStatus(statusOuAtivo(dadosAtualizados.getStatus()));

        if (professora.getUsuario() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Usuario da professora nao encontrado");
        }

        usuarioService.atualizarLogin(professora.getUsuario(), professora.getCref());

        if (professora.getStatus() == StatusCadastro.ATIVO) {
            usuarioService.ativar(professora.getUsuario().getId());
        } else {
            usuarioService.inativar(professora.getUsuario().getId());
        }

        return professoraRepository.save(professora);
    }

    @Transactional
    public Professora atualizarPerfil(Long id, AtualizarPerfilProfessoraRequest dadosAtualizados) {
        Professora professora = buscarPorId(id);

        if (professoraRepository.existsByCrefAndIdNot(dadosAtualizados.getCref(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "CREF ja cadastrado", "cref");
        }

        if (professoraRepository.existsByEmailAndIdNot(dadosAtualizados.getEmail(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "E-mail ja cadastrado", "email");
        }

        if (professora.getUsuario() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Usuario da professora nao encontrado");
        }

        professora.setNome(dadosAtualizados.getNome());
        professora.setEmail(dadosAtualizados.getEmail());
        professora.setCref(dadosAtualizados.getCref());
        professora.setEspecialidade(dadosAtualizados.getEspecialidade());

        usuarioService.atualizarLogin(professora.getUsuario(), professora.getCref());
        usuarioService.atualizarSenha(professora.getUsuario(), dadosAtualizados.getSenha());

        return professoraRepository.save(professora);
    }

    /*
     * Exclui professora.
     *
     * Observacao:
     * Em sistema real, normalmente seria melhor inativar
     * do que excluir definitivamente.
     */
    public void excluir(Long id) {
        Professora professora = buscarPorId(id);
        professoraRepository.delete(professora);
    }

    /*
     * Ativa o cadastro da professora e tambem ativa o Usuario dela.
     *
     * Agora:
     * - Professora.status = ATIVO
     * - Usuario.status = ATIVO
     */
    @Transactional
    public Professora ativar(Long id) {
        Professora professora = buscarPorId(id);

        professora.setStatus(StatusCadastro.ATIVO);

        usuarioService.ativarUsuarioDaProfessora(id);

        return professoraRepository.save(professora);
    }

    /*
     * Inativa o cadastro da professora e tambem inativa o Usuario dela.
     *
     * Isso impede que uma professora INATIVA continue fazendo login.
     *
     * Agora:
     * - Professora.status = INATIVO
     * - Usuario.status = INATIVO
     */
    @Transactional
    public Professora inativar(Long id) {
        Professora professora = buscarPorId(id);

        professora.setStatus(StatusCadastro.INATIVO);

        usuarioService.inativarUsuarioDaProfessora(id);

        return professoraRepository.save(professora);
    }

    private StatusCadastro statusOuAtivo(StatusCadastro status) {
        return status == null ? StatusCadastro.ATIVO : status;
    }
}
