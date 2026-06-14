package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.repositories.ProfessoraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessoraService {

    private final ProfessoraRepository professoraRepository;

    public ProfessoraService(ProfessoraRepository professoraRepository) {
        this.professoraRepository = professoraRepository;
    }

    public List<Professora> listarTodas() {
        return professoraRepository.findAll();
    }

    public Professora buscarPorId(Long id) {
        return professoraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professora nao encontrada"));
    }

    public Professora salvar(Professora professora) {
        return professoraRepository.save(professora);
    }

    public void excluir(Long id) {
        professoraRepository.deleteById(id);
    }

    /*
     * Nova alteracao:
     * Busca uma professora pelo ID,
     * muda o status para ATIVO
     * e salva a alteracao no banco.
     */
    public Professora ativar(Long id) {
        Professora professora = buscarPorId(id);
        professora.setStatus(StatusCadastro.ATIVO);
        return professoraRepository.save(professora);
    }

    /*
     * Nova alteracao:
     * Busca uma professora pelo ID,
     * muda o status para INATIVO
     * e salva a alteracao no banco.
     */
    public Professora inativar(Long id) {
        Professora professora = buscarPorId(id);
        professora.setStatus(StatusCadastro.INATIVO);
        return professoraRepository.save(professora);
    }
}