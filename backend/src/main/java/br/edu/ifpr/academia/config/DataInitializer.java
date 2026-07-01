package br.edu.ifpr.academia.config;

import br.edu.ifpr.academia.repositories.UsuarioRepository;
import br.edu.ifpr.academia.services.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/*
 * Classe executada automaticamente quando a aplicacao inicia.
 *
 * Ela cria um usuario ADMIN inicial caso ainda nao exista.
 *
 * Isso evita que o sistema fique sem nenhum usuario administrador
 * para acessar o painel e cadastrar alunas/professoras.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        String loginAdmin = "admin";
        String senhaAdmin = "admin123";

        if (!usuarioRepository.existsByLogin(loginAdmin)) {
            usuarioService.criarAdmin(loginAdmin, senhaAdmin);

            System.out.println("Usuario ADMIN inicial criado:");
            System.out.println("Login: " + loginAdmin);
            System.out.println("Senha: " + senhaAdmin);
        }
    }
}