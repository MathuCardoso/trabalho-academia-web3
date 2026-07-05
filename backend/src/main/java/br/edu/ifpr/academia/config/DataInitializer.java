package br.edu.ifpr.academia.config;

import br.edu.ifpr.academia.repositories.UsuarioRepository;
import br.edu.ifpr.academia.services.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            JdbcTemplate jdbcTemplate
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        migrarRelacionamentosInvertidos();

        String loginAdmin = "admin";
        String senhaAdmin = "admin123";

        if (!usuarioRepository.existsByLogin(loginAdmin)) {
            usuarioService.criarAdmin(loginAdmin, senhaAdmin);

            System.out.println("Usuario ADMIN inicial criado:");
            System.out.println("Login: " + loginAdmin);
            System.out.println("Senha: " + senhaAdmin);
        }
    }

    private void migrarRelacionamentosInvertidos() {
        if (colunaExiste("usuario", "aluna_id")) {
            jdbcTemplate.update("""
                    UPDATE aluna a
                    SET usuario_id = u.id
                    FROM usuario u
                    WHERE u.aluna_id = a.id AND a.usuario_id IS NULL
                    """);
            jdbcTemplate.update("UPDATE usuario SET aluna_id = NULL WHERE aluna_id IS NOT NULL");
        }

        if (colunaExiste("usuario", "professora_id")) {
            jdbcTemplate.update("""
                    UPDATE professora p
                    SET usuario_id = u.id
                    FROM usuario u
                    WHERE u.professora_id = p.id AND p.usuario_id IS NULL
                    """);
            jdbcTemplate.update("UPDATE usuario SET professora_id = NULL WHERE professora_id IS NOT NULL");
        }
    }

    private boolean colunaExiste(String tabela, String coluna) {
        Integer quantidade = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.columns
                WHERE table_schema = current_schema()
                  AND table_name = ?
                  AND column_name = ?
                """, Integer.class, tabela, coluna);

        return quantidade != null && quantidade > 0;
    }
}
