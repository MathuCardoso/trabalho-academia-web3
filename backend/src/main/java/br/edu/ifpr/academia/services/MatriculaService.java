package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.entities.Matricula;
import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.enums.StatusMatricula;
import br.edu.ifpr.academia.repositories.MatriculaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunaService alunaService;
    private final TreinoService treinoService;

    public MatriculaService(
            MatriculaRepository matriculaRepository,
            AlunaService alunaService,
            TreinoService treinoService
    ) {
        this.matriculaRepository = matriculaRepository;
        this.alunaService = alunaService;
        this.treinoService = treinoService;
    }

    public List<Matricula> listarTodas() {
        return matriculaRepository.findAll();
    }

    public Matricula buscarPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matricula nao encontrada"));
    }

    public Matricula salvar(Matricula matricula) {
        /*
         * Antes de salvar, buscamos a aluna completa no banco.
         * Isso evita retornar aluna apenas com id e campos nulos.
         */
        if (matricula.getAluna() != null && matricula.getAluna().getId() != null) {
            Aluna aluna = alunaService.buscarPorId(matricula.getAluna().getId());
            matricula.setAluna(aluna);
        }

        /*
         * Antes de salvar, buscamos o treino completo no banco.
         * Isso evita retornar treino apenas com id e campos nulos.
         */
        if (matricula.getTreino() != null && matricula.getTreino().getId() != null) {
            Treino treino = treinoService.buscarPorId(matricula.getTreino().getId());
            matricula.setTreino(treino);
        }

        /*
         * Regra:
         * Se a data de vencimento for anterior a data atual,
         * a matricula ja entra como VENCIDA.
         */
        if (matricula.getDataVencimento().isBefore(LocalDate.now())) {
            matricula.setStatus(StatusMatricula.VENCIDA);
        }

        return matriculaRepository.save(matricula);
    }

    public void excluir(Long id) {
        matriculaRepository.deleteById(id);
    }

    public List<Matricula> listarVencidas() {
        return matriculaRepository.findByStatus(StatusMatricula.VENCIDA);
    }

    public List<Matricula> listarAVencer() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);

        /*
         * Nova regra:
         * Lista apenas matriculas ATIVAS que vencem nos proximos 7 dias.
         * Matriculas CANCELADAS nao devem aparecer aqui.
         */
        return matriculaRepository.findByStatusAndDataVencimentoBetween(
                StatusMatricula.ATIVA,
                hoje,
                limite
        );
    }

    public Matricula cancelar(Long id) {
        Matricula matricula = buscarPorId(id);
        matricula.setStatus(StatusMatricula.CANCELADA);
        return matriculaRepository.save(matricula);
    }
}