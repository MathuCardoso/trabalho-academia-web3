package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.entities.Frequencia;
import br.edu.ifpr.academia.repositories.FrequenciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Service responsavel pelas regras de negocio de Frequencia.
 */
@Service
public class FrequenciaService {

    private final FrequenciaRepository frequenciaRepository;
    private final AlunaService alunaService;
    private final MatriculaService matriculaService;

    public FrequenciaService(
            FrequenciaRepository frequenciaRepository,
            AlunaService alunaService,
            MatriculaService matriculaService
    ) {
        this.frequenciaRepository = frequenciaRepository;
        this.alunaService = alunaService;
        this.matriculaService = matriculaService;
    }

    /*
     * Lista todas as frequencias.
     */
    public List<Frequencia> listarTodas() {
        return frequenciaRepository.findAll();
    }

    /*
     * Busca frequencia pelo ID.
     */
    public Frequencia buscarPorId(Long id) {
        return frequenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Frequencia nao encontrada"));
    }

    /*
     * Lista frequencias de uma aluna.
     */
    public List<Frequencia> listarPorAluna(Long alunaId) {
        return frequenciaRepository.findByAlunaId(alunaId);
    }

    /*
     * Salva frequencia.
     *
     * Se dataHoraEntrada vier nula, registra o horario atual.
     */
    public Frequencia salvar(Frequencia frequencia) {
        if (frequencia.getAluna() != null && frequencia.getAluna().getId() != null) {
            Aluna aluna = alunaService.buscarPorId(frequencia.getAluna().getId());
            frequencia.setAluna(aluna);
        }

        if (frequencia.getDataHoraEntrada() == null) {
            frequencia.setDataHoraEntrada(LocalDateTime.now());
        }

        return frequenciaRepository.save(frequencia);
    }

    /*
     * Registra check-in.
     *
     * Regras:
     * - aluna precisa existir
     * - aluna precisa ter matricula ATIVA
     * - data/hora atual e registrada automaticamente
     */
    public Frequencia registrarCheckin(Long alunaId) {
        Aluna aluna = alunaService.buscarPorId(alunaId);

        boolean possuiMatriculaAtiva = matriculaService.alunaPossuiMatriculaAtiva(alunaId);

        if (!possuiMatriculaAtiva) {
            throw new RuntimeException("Aluna nao possui matricula ativa");
        }

        Frequencia frequencia = new Frequencia();
        frequencia.setAluna(aluna);
        frequencia.setDataHoraEntrada(LocalDateTime.now());

        return frequenciaRepository.save(frequencia);
    }

    /*
     * Atualiza frequencia.
     */
    public Frequencia atualizar(Long id, Frequencia dadosAtualizados) {
        Frequencia frequencia = buscarPorId(id);

        if (dadosAtualizados.getAluna() != null && dadosAtualizados.getAluna().getId() != null) {
            Aluna aluna = alunaService.buscarPorId(dadosAtualizados.getAluna().getId());
            frequencia.setAluna(aluna);
        }

        frequencia.setDataHoraEntrada(dadosAtualizados.getDataHoraEntrada());

        return frequenciaRepository.save(frequencia);
    }

    /*
     * Exclui frequencia.
     */
    public void excluir(Long id) {
        frequenciaRepository.deleteById(id);
    }
}