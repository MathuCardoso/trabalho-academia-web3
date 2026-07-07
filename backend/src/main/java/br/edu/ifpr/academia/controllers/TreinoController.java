package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.services.TreinoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 * Controller responsavel pelas rotas de Treino.
 */
@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Treino> listarTodos() {
        return treinoService.listarTodos();
    }

    @PreAuthorize("hasRole('ADMIN') or "
            + "(hasRole('PROFESSORA') and "
            + "@treinoService.pertenceAProfessoraDoUsuario(#id, authentication.name))")
    @GetMapping("/{id}")
    public ResponseEntity<Treino> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.buscarPorId(id));
    }

    @PreAuthorize("hasRole('ADMIN') or "
            + "(hasRole('PROFESSORA') and "
            + "@professoraService.pertenceAoUsuario(#professoraId, authentication.name))")
    @GetMapping("/professora/{professoraId}")
    public List<Treino> listarPorProfessora(@PathVariable Long professoraId) {
        return treinoService.listarPorProfessora(professoraId);
    }

    @PreAuthorize("hasRole('ADMIN') or "
            + "(hasRole('PROFESSORA') and "
            + "@treinoService.professoraInformadaPertenceAoUsuario(#treino, authentication.name))")
    @PostMapping
    public ResponseEntity<Treino> cadastrar(@Valid @RequestBody Treino treino) {
        return ResponseEntity.status(HttpStatus.CREATED).body(treinoService.salvar(treino));
    }

    @PreAuthorize("hasRole('ADMIN') or "
            + "(hasRole('PROFESSORA') and "
            + "@treinoService.pertenceAProfessoraDoUsuario(#id, authentication.name) and "
            + "@treinoService.professoraInformadaPertenceAoUsuario(#treino, authentication.name))")
    @PutMapping("/{id}")
    public ResponseEntity<Treino> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Treino treino
    ) {
        return ResponseEntity.ok(treinoService.atualizar(id, treino));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        treinoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or "
            + "(hasRole('PROFESSORA') and "
            + "@treinoService.pertenceAProfessoraDoUsuario(#id, authentication.name))")
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Treino> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.ativar(id));
    }

    @PreAuthorize("hasRole('ADMIN') or "
            + "(hasRole('PROFESSORA') and "
            + "@treinoService.pertenceAProfessoraDoUsuario(#id, authentication.name))")
    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Treino> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(treinoService.inativar(id));
    }
}
