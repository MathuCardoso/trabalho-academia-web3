package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.dtos.AlunaRequest;
import br.edu.ifpr.academia.dtos.AtualizarPerfilAlunaRequest;
import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.entities.Usuario;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.exceptions.ApiException;
import br.edu.ifpr.academia.repositories.AlunaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Service responsavel pelas regras de negocio de Aluna.
 *
 * Aluna guarda apenas dados cadastrais.
 * Senha e login ficam em Usuario.
 */
@Service
public class AlunaService {

    private final AlunaRepository alunaRepository;
    private final UsuarioService usuarioService;

    public AlunaService(AlunaRepository alunaRepository, UsuarioService usuarioService) {
        this.alunaRepository = alunaRepository;
        this.usuarioService = usuarioService;
    }

    /*
     * Lista todas as alunas.
     */
    public List<Aluna> listarTodas() {
        return alunaRepository.findAll();
    }

    /*
     * Busca aluna pelo ID.
     */
    public Aluna buscarPorId(Long id) {
        return alunaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Aluna nao encontrada"));
    }

    public boolean pertenceAoUsuario(Long alunaId, String login) {
        return alunaRepository.findById(alunaId)
                .map(Aluna::getUsuario)
                .map(Usuario::getLogin)
                .filter(login::equals)
                .isPresent();
    }

    /*
     * Cadastra aluna e cria usuario de acesso automaticamente.
     *
     * Regras:
     * - CPF unico
     * - e-mail unico
     * - cria Aluna
     * - cria Usuario com login = CPF
     * - senha criptografada no Usuario
     * - perfil = ALUNA
     *
     * @Transactional garante que, se der erro ao criar o Usuario,
     * a Aluna tambem nao fica salva sozinha no banco.
     */
    @Transactional
    public Aluna cadastrarComUsuario(AlunaRequest request) {
        if (alunaRepository.existsByCpf(request.getCpf())) {
            throw new ApiException(HttpStatus.CONFLICT, "CPF ja cadastrado", "cpf");
        }

        if (alunaRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "E-mail ja cadastrado", "email");
        }

        Aluna aluna = new Aluna();
        aluna.setNome(request.getNome());
        aluna.setEmail(request.getEmail());
        aluna.setTelefone(request.getTelefone());
        aluna.setCpf(request.getCpf());
        aluna.setDataNascimento(request.getDataNascimento());
        aluna.setStatus(statusOuAtivo(request.getStatus()));

        Usuario usuario = usuarioService.criarUsuarioAluna(aluna, request.getSenhaInicial());
        aluna.setUsuario(usuario);

        return alunaRepository.save(aluna);
    }

    /*
     * Salva aluna diretamente.
     *
     * Mantido para usos internos ou testes.
     * Normalmente, para cadastro via API, usamos cadastrarComUsuario().
     */
    public Aluna salvar(Aluna aluna) {
        return alunaRepository.save(aluna);
    }

    /*
     * Atualiza dados cadastrais da aluna.
     *
     * Nao altera senha.
     * A senha pertence ao Usuario.
     */
    @Transactional
    public Aluna atualizar(Long id, Aluna dadosAtualizados) {
        Aluna aluna = buscarPorId(id);

        if (alunaRepository.existsByCpfAndIdNot(dadosAtualizados.getCpf(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "CPF ja cadastrado", "cpf");
        }

        if (alunaRepository.existsByEmailAndIdNot(dadosAtualizados.getEmail(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "E-mail ja cadastrado", "email");
        }

        aluna.setNome(dadosAtualizados.getNome());
        aluna.setEmail(dadosAtualizados.getEmail());
        aluna.setTelefone(dadosAtualizados.getTelefone());
        aluna.setCpf(dadosAtualizados.getCpf());
        aluna.setDataNascimento(dadosAtualizados.getDataNascimento());
        aluna.setStatus(statusOuAtivo(dadosAtualizados.getStatus()));

        if (aluna.getUsuario() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Usuario da aluna nao encontrado");
        }

        usuarioService.atualizarLogin(aluna.getUsuario(), aluna.getCpf());

        if (aluna.getStatus() == StatusCadastro.ATIVO) {
            usuarioService.ativar(aluna.getUsuario().getId());
        } else {
            usuarioService.inativar(aluna.getUsuario().getId());
        }

        return alunaRepository.save(aluna);
    }

    @Transactional
    public Aluna atualizarPerfil(Long id, AtualizarPerfilAlunaRequest dadosAtualizados) {
        Aluna aluna = buscarPorId(id);

        if (alunaRepository.existsByCpfAndIdNot(dadosAtualizados.getCpf(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "CPF ja cadastrado", "cpf");
        }

        if (alunaRepository.existsByEmailAndIdNot(dadosAtualizados.getEmail(), id)) {
            throw new ApiException(HttpStatus.CONFLICT, "E-mail ja cadastrado", "email");
        }

        if (aluna.getUsuario() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Usuario da aluna nao encontrado");
        }

        aluna.setNome(dadosAtualizados.getNome());
        aluna.setEmail(dadosAtualizados.getEmail());
        aluna.setTelefone(dadosAtualizados.getTelefone());
        aluna.setCpf(dadosAtualizados.getCpf());
        aluna.setDataNascimento(dadosAtualizados.getDataNascimento());

        usuarioService.atualizarLogin(aluna.getUsuario(), aluna.getCpf());
        usuarioService.atualizarSenha(aluna.getUsuario(), dadosAtualizados.getSenha());

        return alunaRepository.save(aluna);
    }

    /*
     * Exclui aluna.
     *
     * Observacao:
     * Em sistema real, normalmente seria melhor inativar
     * do que excluir definitivamente.
     */
    public void excluir(Long id) {
        Aluna aluna = buscarPorId(id);
        alunaRepository.delete(aluna);
    }

    /*
     * Ativa o cadastro da aluna e tambem ativa o Usuario dela.
     *
     * Antes:
     * - Aluna ficava ATIVA
     * - Usuario podia ficar separado
     *
     * Agora:
     * - Aluna.status = ATIVO
     * - Usuario.status = ATIVO
     */
    @Transactional
    public Aluna ativar(Long id) {
        Aluna aluna = buscarPorId(id);

        aluna.setStatus(StatusCadastro.ATIVO);

        usuarioService.ativarUsuarioDaAluna(id);

        return alunaRepository.save(aluna);
    }

    /*
     * Inativa o cadastro da aluna e tambem inativa o Usuario dela.
     *
     * Isso impede que uma aluna INATIVA continue fazendo login.
     *
     * Agora:
     * - Aluna.status = INATIVO
     * - Usuario.status = INATIVO
     */
    @Transactional
    public Aluna inativar(Long id) {
        Aluna aluna = buscarPorId(id);

        aluna.setStatus(StatusCadastro.INATIVO);

        usuarioService.inativarUsuarioDaAluna(id);

        return alunaRepository.save(aluna);
    }

    private StatusCadastro statusOuAtivo(StatusCadastro status) {
        return status == null ? StatusCadastro.ATIVO : status;
    }
}
