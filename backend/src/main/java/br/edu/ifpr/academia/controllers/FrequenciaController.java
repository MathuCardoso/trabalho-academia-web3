package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Frequencia;
import br.edu.ifpr.academia.services.FrequenciaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA')")
    @GetMapping
    public List<Frequencia> listarTodas() {
        return frequenciaService.listarTodas();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA', 'ALUNA')")
    @GetMapping("/{id}")
    public ResponseEntity<Frequencia> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(frequenciaService.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA', 'ALUNA')")
    @GetMapping("/aluna/{alunaId}")
    public List<Frequencia> listarPorAluna(@PathVariable Long alunaId) {
        return frequenciaService.listarPorAluna(alunaId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Frequencia> cadastrar(@Valid @RequestBody Frequencia frequencia) {
        return ResponseEntity.ok(frequenciaService.salvar(frequencia));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ALUNA')")
    @PostMapping("/checkin/{alunaId}")
    public ResponseEntity<Frequencia> registrarCheckin(@PathVariable Long alunaId) {
        return ResponseEntity.ok(frequenciaService.registrarCheckin(alunaId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Frequencia> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Frequencia frequencia
    ) {
        return ResponseEntity.ok(frequenciaService.atualizar(id, frequencia));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        frequenciaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}