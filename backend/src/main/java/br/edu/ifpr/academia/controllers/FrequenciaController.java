package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Frequencia;
import br.edu.ifpr.academia.services.FrequenciaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<Frequencia> cadastrar(@Valid @RequestBody Frequencia frequencia) {
        return ResponseEntity.ok(frequenciaService.salvar(frequencia));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Frequencia> atualizar(@PathVariable Long id, @Valid @RequestBody Frequencia frequencia) {
        frequencia.setId(id);
        return ResponseEntity.ok(frequenciaService.salvar(frequencia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        frequenciaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * Nova alteracao:
     * Registra o check-in de uma aluna pelo ID.
     *
     * Exemplo:
     * POST /api/frequencias/checkin/1
     */
    @PostMapping("/checkin/{alunaId}")
    public ResponseEntity<Frequencia> registrarCheckin(@PathVariable Long alunaId) {
        return ResponseEntity.ok(frequenciaService.registrarCheckin(alunaId));
    }

    /*
     * Nova alteracao:
     * Lista todas as frequencias de uma aluna pelo ID.
     *
     * Exemplo:
     * GET /api/frequencias/aluna/1
     */
    @GetMapping("/aluna/{alunaId}")
    public List<Frequencia> listarPorAluna(@PathVariable Long alunaId) {
        return frequenciaService.listarPorAluna(alunaId);
    }
}