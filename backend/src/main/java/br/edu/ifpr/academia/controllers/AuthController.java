package br.edu.ifpr.academia.controllers;

import br.edu.ifpr.academia.dtos.LoginRequest;
import br.edu.ifpr.academia.dtos.LoginResponse;
import br.edu.ifpr.academia.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Controller responsavel pela autenticacao.
 *
 * Esta rota fica publica no SecurityConfig:
 * /api/auth/login
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}