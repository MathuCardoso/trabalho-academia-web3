package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Frequencia;
import br.edu.ifpr.academia.services.FrequenciaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controller responsavel pelas rotas de Frequencia.
 */
@RestController
@RequestMapping("/api/frequencias")
public class FrequenciaController {

    private final FrequenciaService frequenciaService;

    public FrequenciaController(FrequenciaService frequenciaService) {
        this.frequenciaService = frequenciaService;
    }

    @GetMapping
    public List<Frequencia> listarTodas() {
        return frequenciaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Frequencia> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(frequenciaService.buscarPorId(id));
    }

    /*
     * Lista frequencias de uma aluna.
     */
    @GetMapping("/aluna/{alunaId}")
    public List<Frequencia> listarPorAluna(@PathVariable Long alunaId) {
        return frequenciaService.listarPorAluna(alunaId);
    }

    /*
     * Cadastra frequencia manualmente.
     */
    @PostMapping
    public ResponseEntity<Frequencia> cadastrar(@Valid @RequestBody Frequencia frequencia) {
        return ResponseEntity.ok(frequenciaService.salvar(frequencia));
    }

    /*
     * Registra check-in automatico da aluna.
     *
     * A aluna precisa ter matricula ATIVA.
     */
    @PostMapping("/checkin/{alunaId}")
    public ResponseEntity<Frequencia> registrarCheckin(@PathVariable Long alunaId) {
        return ResponseEntity.ok(frequenciaService.registrarCheckin(alunaId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Frequencia> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Frequencia frequencia
    ) {
        return ResponseEntity.ok(frequenciaService.atualizar(id, frequencia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        frequenciaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}