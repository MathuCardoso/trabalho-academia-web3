package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.services.AlunaService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alunas")
public class AlunaController {

    private final AlunaService alunaService;

    public AlunaController(AlunaService alunaService) {
        this.alunaService = alunaService;
    }

    @GetMapping
    public List<Aluna> listarTodas() {
        return alunaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluna> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alunaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Object> cadastrar(@Valid @RequestBody Aluna aluna) {
        alunaService.salvar(aluna);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Aluna cadastrada com sucesso.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluna> atualizar(@PathVariable Long id, @Valid @RequestBody Aluna aluna) {
        aluna.setId(id);
        return ResponseEntity.ok(alunaService.salvar(aluna));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        alunaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * Nova alteracao:
     * Rota para ativar uma aluna pelo ID.
     *
     * Exemplo:
     * PATCH /api/alunas/1/ativar
     */
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Aluna> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(alunaService.ativar(id));
    }

    /*
     * Nova alteracao:
     * Rota para inativar uma aluna pelo ID.
     *
     * Exemplo:
     * PATCH /api/alunas/1/inativar
     */
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Aluna> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(alunaService.inativar(id));
    }
}