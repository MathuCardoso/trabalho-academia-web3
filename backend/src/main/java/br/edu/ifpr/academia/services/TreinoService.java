package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.exceptions.ApiException;
import br.edu.ifpr.academia.repositories.TreinoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * Service responsavel pelas regras de negocio de Treino.
 */
@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final ProfessoraService professoraService;

    public TreinoService(TreinoRepository treinoRepository, ProfessoraService professoraService) {
        this.treinoRepository = treinoRepository;
        this.professoraService = professoraService;
    }

    /*
     * Lista todos os treinos.
     */
    public List<Treino> listarTodos() {
        return treinoRepository.findAll();
    }

    /*
     * Busca treino pelo ID.
     */
    public Treino buscarPorId(Long id) {
        return treinoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Treino nao encontrado"));
    }

    public boolean pertenceAProfessoraDoUsuario(Long treinoId, String login) {
        return treinoRepository.findById(treinoId)
                .map(Treino::getProfessora)
                .map(Professora::getId)
                .filter(professoraId -> professoraService.pertenceAoUsuario(professoraId, login))
                .isPresent();
    }

    public boolean professoraInformadaPertenceAoUsuario(Treino treino, String login) {
        return treino != null
                && treino.getProfessora() != null
                && treino.getProfessora().getId() != null
                && professoraService.pertenceAoUsuario(treino.getProfessora().getId(), login);
    }

    /*
     * Lista treinos de uma professora.
     */
    public List<Treino> listarPorProfessora(Long professoraId) {
        return treinoRepository.findByProfessoraId(professoraId);
    }

    /*
     * Salva treino.
     *
     * Antes de salvar, busca a professora completa no banco.
     */
    public Treino salvar(Treino treino) {
        treino.setProfessora(buscarProfessoraObrigatoria(treino));

        return treinoRepository.save(treino);
    }

    /*
     * Atualiza treino.
     */
    public Treino atualizar(Long id, Treino dadosAtualizados) {
        Treino treino = buscarPorId(id);

        treino.setNome(dadosAtualizados.getNome());
        treino.setDescricao(dadosAtualizados.getDescricao());
        treino.setNivel(dadosAtualizados.getNivel());

        treino.setProfessora(buscarProfessoraObrigatoria(dadosAtualizados));

        return treinoRepository.save(treino);
    }

    /*
     * Exclui treino.
     */
    public void excluir(Long id) {
        Treino treino = buscarPorId(id);
        treinoRepository.delete(treino);
    }

    /*
     * Ativa treino.
     */
    public Treino ativar(Long id) {
        Treino treino = buscarPorId(id);
        treino.setStatus(StatusCadastro.ATIVO);
        return treinoRepository.save(treino);
    }

    /*
     * Inativa treino.
     */
    public Treino inativar(Long id) {
        Treino treino = buscarPorId(id);
        treino.setStatus(StatusCadastro.INATIVO);
        return treinoRepository.save(treino);
    }

    private Professora buscarProfessoraObrigatoria(Treino treino) {
        if (treino.getProfessora() == null || treino.getProfessora().getId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A professora e obrigatoria", "professora");
        }

        return professoraService.buscarPorId(treino.getProfessora().getId());
    }
}
