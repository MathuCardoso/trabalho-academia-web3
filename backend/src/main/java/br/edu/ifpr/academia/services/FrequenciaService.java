package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.entities.Frequencia;
import br.edu.ifpr.academia.repositories.FrequenciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FrequenciaService {

    private final FrequenciaRepository frequenciaRepository;
    private final AlunaService alunaService;

    public FrequenciaService(FrequenciaRepository frequenciaRepository, AlunaService alunaService) {
        this.frequenciaRepository = frequenciaRepository;
        this.alunaService = alunaService;
    }

    public List<Frequencia> listarTodas() {
        return frequenciaRepository.findAll();
    }

    public Frequencia buscarPorId(Long id) {
        return frequenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Frequencia nao encontrada"));
    }

    public Frequencia salvar(Frequencia frequencia) {
        /*
         * Regra:
         * Se a data/hora de entrada nao for enviada,
         * o sistema registra automaticamente o horario atual.
         */
        if (frequencia.getDataHoraEntrada() == null) {
            frequencia.setDataHoraEntrada(LocalDateTime.now());
        }

        return frequenciaRepository.save(frequencia);
    }

    public void excluir(Long id) {
        frequenciaRepository.deleteById(id);
    }

    /*
     * Nova alteracao:
     * Registra o check-in de uma aluna pelo ID.
     *
     * O sistema busca a aluna, cria uma nova frequencia
     * e registra automaticamente a data/hora atual.
     */
    public Frequencia registrarCheckin(Long alunaId) {
        Aluna aluna = alunaService.buscarPorId(alunaId);

        Frequencia frequencia = new Frequencia();
        frequencia.setAluna(aluna);
        frequencia.setDataHoraEntrada(LocalDateTime.now());

        return frequenciaRepository.save(frequencia);
    }

    /*
     * Nova alteracao:
     * Lista todas as frequencias de uma aluna especifica.
     */
    public List<Frequencia> listarPorAluna(Long alunaId) {
        return frequenciaRepository.findByAlunaId(alunaId);
    }
}