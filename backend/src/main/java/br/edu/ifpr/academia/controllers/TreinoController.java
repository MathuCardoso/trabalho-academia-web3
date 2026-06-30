package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.services.TreinoService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Object> cadastrar(@Valid @RequestBody Treino treino) {
        treinoService.salvar(treino);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Treino cadastrado com sucesso.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);    }

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
}