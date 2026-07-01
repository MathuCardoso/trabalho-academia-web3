package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.dtos.AlunaRequest;
import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.services.AlunaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public List<Aluna> listarTodas() {
        return alunaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aluna> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alunaService.buscarPorId(id));
    }

    /*
     * Cadastra uma aluna e cria automaticamente o Usuario dela.
     *
     * O front deve enviar:
     * - nome
     * - email
     * - telefone
     * - cpf
     * - dataNascimento
     * - senhaInicial
     */
    @PostMapping
    public ResponseEntity<Aluna> cadastrar(@Valid @RequestBody AlunaRequest request) {
        return ResponseEntity.ok(alunaService.cadastrarComUsuario(request));
    }

    /*
     * Atualiza apenas dados cadastrais.
     *
     * Nao altera senha.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Aluna> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Aluna aluna
    ) {
        return ResponseEntity.ok(alunaService.atualizar(id, aluna));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        alunaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Aluna> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(alunaService.ativar(id));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Aluna> inativar(@PathVariable Long id) {
        return ResponseEntity.ok(alunaService.inativar(id));
    }
}