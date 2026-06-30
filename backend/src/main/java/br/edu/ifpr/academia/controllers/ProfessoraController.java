package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.services.ProfessoraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/professoras")
public class ProfessoraController {

    private final ProfessoraService professoraService;

    public ProfessoraController(ProfessoraService professoraService) {
        this.professoraService = professoraService;
    }

    @GetMapping
    public List<Professora> listarTodas() {
        return professoraService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professora> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(professoraService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Object> cadastrar(@Valid @RequestBody Professora professora) {
        professoraService.salvar(professora);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Professora cadastrada com sucesso.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @Valid @RequestBody Professora professora) {
        professora.setId(id);
        professoraService.salvar(professora);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Professora alterada com sucesso.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        professoraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    /*
     * Nova alteracao:
     * Rota para ativar uma professora pelo ID.
     *
     * Exemplo:
     * PATCH /api/professoras/1/ativar
     */
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Professora> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(professoraService.ativar(id));
    }

    /*
     * Nova alteracao:
     * Rota para inativar uma professora pelo ID.
     *
     * Exemplo:
     * PATCH /api/professoras/1/inativar
     */
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Professora> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(professoraService.inativar(id));
    }
}