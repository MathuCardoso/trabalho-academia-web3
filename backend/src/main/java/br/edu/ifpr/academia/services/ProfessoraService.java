package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.dtos.ProfessoraRequest;
import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.repositories.ProfessoraRepository;
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
                .orElseThrow(() -> new RuntimeException("Professora nao encontrada"));
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
            throw new RuntimeException("CREF ja cadastrado");
        }

        if (professoraRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("E-mail ja cadastrado");
        }

        Professora professora = new Professora();
        professora.setNome(request.getNome());
        professora.setEmail(request.getEmail());
        professora.setCref(request.getCref());
        professora.setEspecialidade(request.getEspecialidade());
        professora.setStatus(StatusCadastro.ATIVO);

        Professora professoraSalva = professoraRepository.save(professora);

        usuarioService.criarUsuarioProfessora(professoraSalva, request.getSenhaInicial());

        return professoraSalva;
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
    public Professora atualizar(Long id, Professora dadosAtualizados) {
        Professora professora = buscarPorId(id);

        professora.setNome(dadosAtualizados.getNome());
        professora.setEmail(dadosAtualizados.getEmail());
        professora.setCref(dadosAtualizados.getCref());
        professora.setEspecialidade(dadosAtualizados.getEspecialidade());

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
        professoraRepository.deleteById(id);
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
}