package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.dtos.ProfessoraRequest;
import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.services.ProfessoraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public List<Professora> listarTodas() {
        return professoraService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professora> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(professoraService.buscarPorId(id));
    }

    /*
     * Cadastra uma professora e cria automaticamente o Usuario dela.
     *
     * O front deve enviar:
     * - nome
     * - email
     * - cref
     * - especialidade
     * - senhaInicial
     */
    @PostMapping
    public ResponseEntity<Professora> cadastrar(@Valid @RequestBody ProfessoraRequest request) {
        return ResponseEntity.ok(professoraService.cadastrarComUsuario(request));
    }

    /*
     * Atualiza apenas dados profissionais.
     *
     * Nao altera senha.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Professora> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Professora professora
    ) {
        return ResponseEntity.ok(professoraService.atualizar(id, professora));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        professoraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Professora> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(professoraService.ativar(id));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Professora> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(professoraService.inativar(id));
    }
}