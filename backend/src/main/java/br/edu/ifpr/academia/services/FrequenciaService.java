package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.entities.Frequencia;
import br.edu.ifpr.academia.exceptions.ApiException;
import br.edu.ifpr.academia.repositories.FrequenciaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<Frequencia> listarTodas() {
        return frequenciaRepository.findAll();
    }

    public Frequencia buscarPorId(Long id) {
        return frequenciaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Frequencia nao encontrada"));
    }

    public boolean pertenceAAlunaDoUsuario(Long frequenciaId, String login) {
        return frequenciaRepository.findById(frequenciaId)
                .map(Frequencia::getAluna)
                .map(Aluna::getId)
                .filter(alunaId -> alunaService.pertenceAoUsuario(alunaId, login))
                .isPresent();
    }

    public List<Frequencia> listarPorAluna(Long alunaId) {
        return frequenciaRepository.findByAlunaId(alunaId);
    }

    public Frequencia salvar(Frequencia frequencia) {
        frequencia.setAluna(buscarAlunaObrigatoria(frequencia));

        matriculaService.validarAlunaPossuiMatriculaAtiva(frequencia.getAluna().getId());

        if (frequencia.getDataHoraEntrada() == null) {
            frequencia.setDataHoraEntrada(LocalDateTime.now());
        }

        return frequenciaRepository.save(frequencia);
    }

    public Frequencia registrarCheckin(Long alunaId) {
        Aluna aluna = alunaService.buscarPorId(alunaId);

        matriculaService.validarAlunaPossuiMatriculaAtiva(alunaId);

        Frequencia frequencia = new Frequencia();
        frequencia.setAluna(aluna);
        frequencia.setDataHoraEntrada(LocalDateTime.now());

        return frequenciaRepository.save(frequencia);
    }

    public Frequencia atualizar(Long id, Frequencia dadosAtualizados) {
        Frequencia frequencia = buscarPorId(id);

        frequencia.setAluna(buscarAlunaObrigatoria(dadosAtualizados));

        matriculaService.validarAlunaPossuiMatriculaAtiva(frequencia.getAluna().getId());

        frequencia.setDataHoraEntrada(dadosAtualizados.getDataHoraEntrada());

        if (frequencia.getDataHoraEntrada() == null) {
            frequencia.setDataHoraEntrada(LocalDateTime.now());
        }

        return frequenciaRepository.save(frequencia);
    }

    public void excluir(Long id) {
        Frequencia frequencia = buscarPorId(id);
        frequenciaRepository.delete(frequencia);
    }

    private Aluna buscarAlunaObrigatoria(Frequencia frequencia) {
        if (frequencia.getAluna() == null || frequencia.getAluna().getId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A aluna e obrigatoria", "aluna");
        }

        return alunaService.buscarPorId(frequencia.getAluna().getId());
    }
}