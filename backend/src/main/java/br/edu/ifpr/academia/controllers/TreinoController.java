package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.services.TreinoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @GetMapping
    public List<Treino> listarTodos() {
        return treinoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Treino> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Treino> cadastrar(@Valid @RequestBody Treino treino) {
        return ResponseEntity.ok(treinoService.salvar(treino));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Treino> atualizar(@PathVariable Long id, @Valid @RequestBody Treino treino) {
        treino.setId(id);
        return ResponseEntity.ok(treinoService.salvar(treino));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        treinoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * Nova alteracao:
     * Rota para ativar um treino pelo ID.
     *
     * Exemplo:
     * PATCH /api/treinos/1/ativar
     */
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Treino> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.ativar(id));
    }

    /*
     * Nova alteracao:
     * Rota para inativar um treino pelo ID.
     *
     * Exemplo:
     * PATCH /api/treinos/1/inativar
     */
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Treino> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.inativar(id));
    }
}