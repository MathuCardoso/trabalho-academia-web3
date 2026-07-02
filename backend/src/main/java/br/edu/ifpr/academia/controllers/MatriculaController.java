package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Matricula;
import br.edu.ifpr.academia.services.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controller responsavel pelas rotas de Matricula.
 */
@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA')")
    @GetMapping
    public List<Matricula> listarTodas() {
        return matriculaService.listarTodas();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA', 'ALUNA')")
    @GetMapping("/{id}")
    public ResponseEntity<Matricula> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.buscarPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA', 'ALUNA')")
    @GetMapping("/aluna/{alunaId}")
    public List<Matricula> listarPorAluna(@PathVariable Long alunaId) {
        return matriculaService.listarPorAluna(alunaId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA')")
    @GetMapping("/professora/{professoraId}")
    public List<Matricula> listarPorProfessora(@PathVariable Long professoraId) {
        return matriculaService.listarPorProfessora(professoraId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA')")
    @GetMapping("/vencidas")
    public List<Matricula> listarVencidas() {
        return matriculaService.listarVencidas();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA')")
    @GetMapping("/a-vencer")
    public List<Matricula> listarAVencer() {
        return matriculaService.listarAVencer();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Matricula> cadastrar(@Valid @RequestBody Matricula matricula) {
        return ResponseEntity.ok(matriculaService.salvar(matricula));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Matricula> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Matricula matricula
    ) {
        return ResponseEntity.ok(matriculaService.atualizar(id, matricula));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Matricula> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.cancelar(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        matriculaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}