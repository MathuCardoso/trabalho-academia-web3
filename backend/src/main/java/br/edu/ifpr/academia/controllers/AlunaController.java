package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.dtos.AlunaRequest;
import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.services.AlunaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controller responsavel pelas rotas de Aluna.
 */
@RestController
@RequestMapping("/api/alunas")
public class AlunaController {

    private final AlunaService alunaService;

    public AlunaController(AlunaService alunaService) {
        this.alunaService = alunaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA')")
    @GetMapping
    public List<Aluna> listarTodas() {
        return alunaService.listarTodas();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSORA', 'ALUNA')")
    @GetMapping("/{id}")
    public ResponseEntity<Aluna> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alunaService.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Aluna> cadastrar(@Valid @RequestBody AlunaRequest request) {
        return ResponseEntity.ok(alunaService.cadastrarComUsuario(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Aluna> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Aluna aluna
    ) {
        return ResponseEntity.ok(alunaService.atualizar(id, aluna));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        alunaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Aluna> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(alunaService.ativar(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Aluna> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(alunaService.inativar(id));
    }
}