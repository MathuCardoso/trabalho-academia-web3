package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.dtos.LoginRequest;
import br.edu.ifpr.academia.dtos.LoginResponse;
import br.edu.ifpr.academia.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Controller responsavel pelas rotas de autenticacao.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /*
     * Rota de login.
     *
     * Recebe:
     * - login
     * - senha
     *
     * Retorna:
     * - id do usuario
     * - perfil
     * - nome
     * - id da aluna ou professora, quando existir
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}