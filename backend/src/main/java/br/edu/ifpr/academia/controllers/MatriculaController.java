package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Matricula;
import br.edu.ifpr.academia.services.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @GetMapping
    public List<Matricula> listarTodas() {
        return matriculaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matricula> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Matricula> cadastrar(@Valid @RequestBody Matricula matricula) {
        return ResponseEntity.ok(matriculaService.salvar(matricula));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Matricula> atualizar(@PathVariable Long id, @Valid @RequestBody Matricula matricula) {
        matricula.setId(id);
        return ResponseEntity.ok(matriculaService.salvar(matricula));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        matriculaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * Nova alteracao:
     * Lista matrículas com status VENCIDA.
     *
     * Exemplo:
     * GET /api/matriculas/vencidas
     */
    @GetMapping("/vencidas")
    public List<Matricula> listarVencidas() {
        return matriculaService.listarVencidas();
    }

    /*
     * Nova alteracao:
     * Lista matrículas que vencem nos próximos 7 dias.
     *
     * Exemplo:
     * GET /api/matriculas/a-vencer
     */
    @GetMapping("/a-vencer")
    public List<Matricula> listarAVencer() {
        return matriculaService.listarAVencer();
    }

    /*
     * Nova alteracao:
     * Cancela uma matrícula pelo ID.
     *
     * Exemplo:
     * PATCH /api/matriculas/1/cancelar
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Matricula> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.cancelar(id));
    }
}