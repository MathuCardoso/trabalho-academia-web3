package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Matricula;
import br.edu.ifpr.academia.services.MatriculaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public List<Matricula> listarTodas() {
        return matriculaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matricula> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.buscarPorId(id));
    }

    /*
     * Lista matriculas de uma aluna.
     */
    @GetMapping("/aluna/{alunaId}")
    public List<Matricula> listarPorAluna(@PathVariable Long alunaId) {
        return matriculaService.listarPorAluna(alunaId);
    }

    /*
     * Lista matriculas de alunas vinculadas aos treinos de uma professora.
     */
    @GetMapping("/professora/{professoraId}")
    public List<Matricula> listarPorProfessora(@PathVariable Long professoraId) {
        return matriculaService.listarPorProfessora(professoraId);
    }

    /*
     * Lista matriculas vencidas.
     */
    @GetMapping("/vencidas")
    public List<Matricula> listarVencidas() {
        return matriculaService.listarVencidas();
    }

    /*
     * Lista matriculas ativas que vencem nos proximos 7 dias.
     */
    @GetMapping("/a-vencer")
    public List<Matricula> listarAVencer() {
        return matriculaService.listarAVencer();
    }

    /*
     * Cadastra matricula.
     *
     * O JSON deve enviar:
     * - aluna com id
     * - treino com id
     * - dataInicio
     * - dataVencimento
     */
    @PostMapping
    public ResponseEntity<Matricula> cadastrar(@Valid @RequestBody Matricula matricula) {
        return ResponseEntity.ok(matriculaService.salvar(matricula));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Matricula> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Matricula matricula
    ) {
        return ResponseEntity.ok(matriculaService.atualizar(id, matricula));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        matriculaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Matricula> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.cancelar(id));
    }
}