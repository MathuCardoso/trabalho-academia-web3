package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.dtos.ProfessoraRequest;
import br.edu.ifpr.academia.dtos.AtualizarPerfilProfessoraRequest;
import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.services.ProfessoraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controller responsavel pelas rotas de Professora.
 */
@RestController
@RequestMapping("/api/professoras")
public class ProfessoraController {

    private final ProfessoraService professoraService;

    public ProfessoraController(ProfessoraService professoraService) {
        this.professoraService = professoraService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Professora> listarTodas() {
        return professoraService.listarTodas();
    }

    @PreAuthorize("hasRole('ADMIN') or "
            + "(hasRole('PROFESSORA') and @professoraService.pertenceAoUsuario(#id, authentication.name))")
    @GetMapping("/{id}")
    public ResponseEntity<Professora> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(professoraService.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Professora> cadastrar(@Valid @RequestBody ProfessoraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(professoraService.cadastrarComUsuario(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Professora> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Professora professora
    ) {
        return ResponseEntity.ok(professoraService.atualizar(id, professora));
    }

    @PreAuthorize("hasRole('PROFESSORA') and "
            + "@professoraService.pertenceAoUsuario(#id, authentication.name)")
    @PutMapping("/{id}/perfil")
    public ResponseEntity<Professora> atualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarPerfilProfessoraRequest request
    ) {
        return ResponseEntity.ok(professoraService.atualizarPerfil(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        professoraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Professora> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(professoraService.ativar(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Professora> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(professoraService.inativar(id));
    }
}
