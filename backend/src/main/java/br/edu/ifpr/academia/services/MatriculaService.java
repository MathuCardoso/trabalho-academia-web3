package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.entities.Matricula;
import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.enums.StatusMatricula;
import br.edu.ifpr.academia.repositories.MatriculaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/*
 * Service responsavel pelas regras de negocio de Matricula.
 */
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

    /*
     * Lista todas as matriculas.
     */
    public List<Matricula> listarTodas() {
        return matriculaRepository.findAll();
    }

    /*
     * Busca matricula pelo ID.
     */
    public Matricula buscarPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matricula nao encontrada"));
    }

    /*
     * Lista matriculas de uma aluna.
     */
    public List<Matricula> listarPorAluna(Long alunaId) {
        return matriculaRepository.findByAlunaId(alunaId);
    }

    /*
     * Lista matriculas de alunas vinculadas aos treinos de uma professora.
     */
    public List<Matricula> listarPorProfessora(Long professoraId) {
        return matriculaRepository.findByTreinoProfessoraId(professoraId);
    }

    /*
     * Salva matricula.
     *
     * Regras:
     * - buscar Aluna completa
     * - buscar Treino completo
     * - se vencimento passou, status = VENCIDA
     */
    public Matricula salvar(Matricula matricula) {
        if (matricula.getAluna() != null && matricula.getAluna().getId() != null) {
            Aluna aluna = alunaService.buscarPorId(matricula.getAluna().getId());
            matricula.setAluna(aluna);
        }

        if (matricula.getTreino() != null && matricula.getTreino().getId() != null) {
            Treino treino = treinoService.buscarPorId(matricula.getTreino().getId());
            matricula.setTreino(treino);
        }

        if (matricula.getDataVencimento().isBefore(LocalDate.now())) {
            matricula.setStatus(StatusMatricula.VENCIDA);
        }

        return matriculaRepository.save(matricula);
    }

    /*
     * Atualiza matricula.
     */
    public Matricula atualizar(Long id, Matricula dadosAtualizados) {
        Matricula matricula = buscarPorId(id);

        if (dadosAtualizados.getAluna() != null && dadosAtualizados.getAluna().getId() != null) {
            Aluna aluna = alunaService.buscarPorId(dadosAtualizados.getAluna().getId());
            matricula.setAluna(aluna);
        }

        if (dadosAtualizados.getTreino() != null && dadosAtualizados.getTreino().getId() != null) {
            Treino treino = treinoService.buscarPorId(dadosAtualizados.getTreino().getId());
            matricula.setTreino(treino);
        }

        matricula.setDataInicio(dadosAtualizados.getDataInicio());
        matricula.setDataVencimento(dadosAtualizados.getDataVencimento());

        if (dadosAtualizados.getDataVencimento().isBefore(LocalDate.now())) {
            matricula.setStatus(StatusMatricula.VENCIDA);
        } else {
            matricula.setStatus(dadosAtualizados.getStatus());
        }

        return matriculaRepository.save(matricula);
    }

    /*
     * Exclui matricula.
     */
    public void excluir(Long id) {
        matriculaRepository.deleteById(id);
    }

    /*
     * Lista matriculas vencidas.
     */
    public List<Matricula> listarVencidas() {
        return matriculaRepository.findByStatus(StatusMatricula.VENCIDA);
    }

    /*
     * Lista matriculas que vencem nos proximos 7 dias.
     *
     * Traz apenas matriculas ATIVAS.
     */
    public List<Matricula> listarAVencer() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);

        return matriculaRepository.findByStatusAndDataVencimentoBetween(
                StatusMatricula.ATIVA,
                hoje,
                limite
        );
    }

    /*
     * Cancela matricula.
     */
    public Matricula cancelar(Long id) {
        Matricula matricula = buscarPorId(id);
        matricula.setStatus(StatusMatricula.CANCELADA);
        return matriculaRepository.save(matricula);
    }

    /*
     * Verifica se uma aluna possui matricula ativa.
     *
     * Usado para validar check-in.
     */
    public boolean alunaPossuiMatriculaAtiva(Long alunaId) {
        return matriculaRepository.existsByAlunaIdAndStatus(alunaId, StatusMatricula.ATIVA);
    }
}