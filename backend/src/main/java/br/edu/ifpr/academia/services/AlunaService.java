package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.repositories.AlunaRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlunaService {

    private final AlunaRepository alunaRepository;

    public AlunaService(AlunaRepository alunaRepository) {
        this.alunaRepository = alunaRepository;
    }

    public List<Aluna> listarTodas() {
        return alunaRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Aluna buscarPorId(Long id) {
        return alunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluna nao encontrada"));
    }

    public Aluna salvar(Aluna aluna) {
        return alunaRepository.save(aluna);
    }

    public void excluir(Long id) {
        alunaRepository.deleteById(id);
    }

    /*
     * Nova alteracao:
     * Este metodo busca uma aluna pelo ID,
     * muda o status dela para ATIVO
     * e salva a alteracao no banco.
     *
     * Sera usado em uma rota do tipo:
     * PATCH /api/alunas/{id}/ativar
     */
    public Aluna ativar(Long id) {
        Aluna aluna = buscarPorId(id);
        aluna.setStatus(StatusCadastro.ATIVO);
        return alunaRepository.save(aluna);
    }

    /*
     * Nova alteracao:
     * Este metodo busca uma aluna pelo ID,
     * muda o status dela para INATIVO
     * e salva a alteracao no banco.
     *
     * Sera usado em uma rota do tipo:
     * PATCH /api/alunas/{id}/inativar
     */
    public Aluna inativar(Long id) {
        Aluna aluna = buscarPorId(id);
        aluna.setStatus(StatusCadastro.INATIVO);
        return alunaRepository.save(aluna);
    }
}