package br.edu.ifpr.academia.config;

import br.edu.ifpr.academia.dtos.AlunaRequest;
import br.edu.ifpr.academia.dtos.ProfessoraRequest;
import br.edu.ifpr.academia.entities.Aluna;
import br.edu.ifpr.academia.entities.Frequencia;
import br.edu.ifpr.academia.entities.Matricula;
import br.edu.ifpr.academia.entities.Professora;
import br.edu.ifpr.academia.entities.Treino;
import br.edu.ifpr.academia.enums.NivelTreino;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.enums.StatusMatricula;
import br.edu.ifpr.academia.repositories.FrequenciaRepository;
import br.edu.ifpr.academia.repositories.MatriculaRepository;
import br.edu.ifpr.academia.repositories.UsuarioRepository;
import br.edu.ifpr.academia.services.AlunaService;
import br.edu.ifpr.academia.services.MatriculaService;
import br.edu.ifpr.academia.services.ProfessoraService;
import br.edu.ifpr.academia.services.TreinoService;
import br.edu.ifpr.academia.services.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private final AlunaService alunaService;
    private final ProfessoraService professoraService;
    private final TreinoService treinoService;
    private final MatriculaService matriculaService;
    private final MatriculaRepository matriculaRepository;
    private final FrequenciaRepository frequenciaRepository;

    public DataInitializer(
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            JdbcTemplate jdbcTemplate,
            AlunaService alunaService,
            ProfessoraService professoraService,
            TreinoService treinoService,
            MatriculaService matriculaService,
            MatriculaRepository matriculaRepository,
            FrequenciaRepository frequenciaRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.jdbcTemplate = jdbcTemplate;
        this.alunaService = alunaService;
        this.professoraService = professoraService;
        this.treinoService = treinoService;
        this.matriculaService = matriculaService;
        this.matriculaRepository = matriculaRepository;
        this.frequenciaRepository = frequenciaRepository;
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

        popularDadosDeTeste();
    }

    private void popularDadosDeTeste() {
        Professora professora = obterOuCriarProfessora();
        Aluna alunaAtiva = obterOuCriarAluna(
                "529.982.247-25",
                "Mariana Alves",
                "mariana.alves@bellafit.test",
                LocalDate.of(1998, 4, 12)
        );
        Aluna alunaComMatriculaVencida = obterOuCriarAluna(
                "111.444.777-35",
                "Carolina Mendes",
                "carolina.mendes@bellafit.test",
                LocalDate.of(1995, 9, 23)
        );

        List<Treino> treinos = treinoService.listarPorProfessora(professora.getId());
        Treino treinoAtivo = obterOuCriarTreino(
                treinos,
                professora,
                "Treino Funcional",
                "Treino funcional completo para condicionamento e mobilidade",
                NivelTreino.INTERMEDIARIO,
                StatusCadastro.ATIVO
        );
        obterOuCriarTreino(
                treinos,
                professora,
                "Treino de Adaptacao",
                "Sequencia introdutoria para novas alunas",
                NivelTreino.INICIANTE,
                StatusCadastro.INATIVO
        );

        obterOuCriarMatricula(
                alunaAtiva,
                treinoAtivo,
                LocalDate.now().minusDays(15),
                LocalDate.now().plusDays(30)
        );
        obterOuCriarMatricula(
                alunaComMatriculaVencida,
                treinoAtivo,
                LocalDate.now().minusDays(60),
                LocalDate.now().minusDays(5)
        );

        if (frequenciaRepository.findByAlunaId(alunaAtiva.getId()).isEmpty()) {
            for (int dias : List.of(8, 4, 1)) {
                Frequencia frequencia = new Frequencia();
                frequencia.setAluna(alunaAtiva);
                frequencia.setDataHoraEntrada(
                        LocalDateTime.now().minusDays(dias).withHour(18).withMinute(30)
                );
                frequenciaRepository.save(frequencia);
            }
        }
    }

    private Professora obterOuCriarProfessora() {
        String login = "123456-G/PR";
        if (usuarioRepository.existsByLogin(login)) {
            return usuarioService.buscarProfessoraPorUsuario(
                    usuarioService.buscarPorLogin(login).getId()
            );
        }

        ProfessoraRequest request = new ProfessoraRequest();
        request.setNome("Fernanda Ribeiro");
        request.setEmail("fernanda.ribeiro@bellafit.test");
        request.setCref(login);
        request.setEspecialidade("Treinamento funcional");
        request.setSenhaInicial("prof123");
        request.setStatus(StatusCadastro.ATIVO);
        return professoraService.cadastrarComUsuario(request);
    }

    private Aluna obterOuCriarAluna(
            String cpf,
            String nome,
            String email,
            LocalDate dataNascimento
    ) {
        if (usuarioRepository.existsByLogin(cpf)) {
            return usuarioService.buscarAlunaPorUsuario(
                    usuarioService.buscarPorLogin(cpf).getId()
            );
        }

        AlunaRequest request = new AlunaRequest();
        request.setNome(nome);
        request.setEmail(email);
        request.setTelefone("45 99999-1111");
        request.setCpf(cpf);
        request.setDataNascimento(dataNascimento);
        request.setSenhaInicial("aluna123");
        request.setStatus(StatusCadastro.ATIVO);
        return alunaService.cadastrarComUsuario(request);
    }

    private Treino obterOuCriarTreino(
            List<Treino> treinos,
            Professora professora,
            String nome,
            String descricao,
            NivelTreino nivel,
            StatusCadastro status
    ) {
        return treinos.stream()
                .filter(treino -> treino.getNome().equals(nome))
                .findFirst()
                .orElseGet(() -> {
                    Treino treino = new Treino();
                    treino.setNome(nome);
                    treino.setDescricao(descricao);
                    treino.setNivel(nivel);
                    treino.setStatus(status);
                    treino.setProfessora(professora);
                    return treinoService.salvar(treino);
                });
    }

    private Matricula obterOuCriarMatricula(
            Aluna aluna,
            Treino treino,
            LocalDate inicio,
            LocalDate vencimento
    ) {
        return matriculaRepository.findByAlunaId(aluna.getId()).stream()
                .findFirst()
                .orElseGet(() -> {
                    Matricula matricula = new Matricula();
                    matricula.setAluna(aluna);
                    matricula.setTreino(treino);
                    matricula.setDataInicio(inicio);
                    matricula.setDataVencimento(vencimento);
                    matricula.setStatus(StatusMatricula.ATIVA);
                    return matriculaService.salvar(matricula);
                });
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
