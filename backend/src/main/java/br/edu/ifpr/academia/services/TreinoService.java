package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.repositories.TreinoRepository;
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
                .orElseThrow(() -> new RuntimeException("Treino nao encontrado"));
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
        if (treino.getProfessora() != null && treino.getProfessora().getId() != null) {
            Professora professora = professoraService.buscarPorId(treino.getProfessora().getId());
            treino.setProfessora(professora);
        }

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

        if (dadosAtualizados.getProfessora() != null && dadosAtualizados.getProfessora().getId() != null) {
            Professora professora = professoraService.buscarPorId(dadosAtualizados.getProfessora().getId());
            treino.setProfessora(professora);
        }

        return treinoRepository.save(treino);
    }

    /*
     * Exclui treino.
     */
    public void excluir(Long id) {
        treinoRepository.deleteById(id);
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
}