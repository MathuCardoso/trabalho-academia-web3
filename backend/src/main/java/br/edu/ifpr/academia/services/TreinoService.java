package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.repositories.TreinoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final ProfessoraService professoraService;

    public TreinoService(TreinoRepository treinoRepository, ProfessoraService professoraService) {
        this.treinoRepository = treinoRepository;
        this.professoraService = professoraService;
    }

    public List<Treino> listarTodos() {
        return treinoRepository.findAll();
    }

    public Treino buscarPorId(Long id) {
        return treinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Treino nao encontrado"));
    }

    public Treino salvar(Treino treino) {
        /*
         * Nova alteracao:
         * Antes de salvar o treino, buscamos a professora completa no banco.
         *
         * Isso evita salvar apenas:
         * professora: { id: 1 }
         *
         * E garante que o retorno venha com os dados reais da professora.
         */
        if (treino.getProfessora() != null && treino.getProfessora().getId() != null) {
            Professora professora = professoraService.buscarPorId(treino.getProfessora().getId());
            treino.setProfessora(professora);
        }

        return treinoRepository.save(treino);
    }

    public void excluir(Long id) {
        treinoRepository.deleteById(id);
    }

    public Treino ativar(Long id) {
        Treino treino = buscarPorId(id);
        treino.setStatus(StatusCadastro.ATIVO);
        return treinoRepository.save(treino);
    }

    public Treino inativar(Long id) {
        Treino treino = buscarPorId(id);
        treino.setStatus(StatusCadastro.INATIVO);
        return treinoRepository.save(treino);
    }
}