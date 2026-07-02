package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.dtos.LoginRequest;
import br.edu.ifpr.academia.dtos.LoginResponse;
import br.edu.ifpr.academia.entities.Usuario;
import br.edu.ifpr.academia.enums.PerfilUsuario;
import br.edu.ifpr.academia.enums.StatusCadastro;
import org.springframework.stereotype.Service;

/*
 * Service responsavel pela autenticacao.
 *
 * Aqui fica a regra de login:
 * - buscar Usuario pelo login
 * - verificar se o Usuario esta ATIVO
 * - comparar a senha enviada com a senha criptografada
 * - gerar o token JWT
 * - devolver os dados do usuario logado para o front-end
 */
@Service
public class AuthService {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    /*
     * Injetamos UsuarioService para buscar usuario e validar senha.
     * Injetamos JwtService para gerar o token JWT apos o login correto.
     */
    public AuthService(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    /*
     * Realiza login do usuario.
     *
     * O login pode ser:
     * - admin
     * - CPF da aluna
     * - CREF da professora
     *
     * Se login e senha estiverem corretos, gera um token JWT.
     */
    public LoginResponse login(LoginRequest request) {
        /*
         * Busca o usuario pelo login informado.
         *
         * Se nao encontrar, o UsuarioService lanca erro.
         */
        Usuario usuario = usuarioService.buscarPorLogin(request.getLogin());

        /*
         * Usuario INATIVO nao pode fazer login.
         */
        if (usuario.getStatus() == StatusCadastro.INATIVO) {
            throw new RuntimeException("Usuario inativo");
        }

        /*
         * Compara a senha enviada com a senha criptografada salva no banco.
         *
         * request.getSenha() = senha digitada no login
         * usuario.getSenha() = senha criptografada no banco
         */
        boolean senhaValida = usuarioService.getPasswordEncoder()
                .matches(request.getSenha(), usuario.getSenha());

        if (!senhaValida) {
            throw new RuntimeException("Login ou senha invalidos");
        }

        /*
         * Define os dados extras que o front-end vai usar.
         *
         * ADMIN:
         * - nome = Administrador
         * - alunaId = null
         * - professoraId = null
         *
         * ALUNA:
         * - nome = nome da aluna
         * - alunaId = id da aluna
         *
         * PROFESSORA:
         * - nome = nome da professora
         * - professoraId = id da professora
         */
        Long alunaId = null;
        Long professoraId = null;
        String nome = "Administrador";

        if (usuario.getPerfil() == PerfilUsuario.ALUNA && usuario.getAluna() != null) {
            alunaId = usuario.getAluna().getId();
            nome = usuario.getAluna().getNome();
        }

        if (usuario.getPerfil() == PerfilUsuario.PROFESSORA && usuario.getProfessora() != null) {
            professoraId = usuario.getProfessora().getId();
            nome = usuario.getProfessora().getNome();
        }

        /*
         * Gera o token JWT.
         *
         * Esse token sera enviado ao front-end.
         * Depois, o front-end devera enviar esse token nas proximas requisicoes:
         *
         * Authorization: Bearer TOKEN
         */
        String token = jwtService.gerarToken(usuario);

        /*
         * Retorna a resposta do login com:
         * - token JWT
         * - dados do usuario
         * - perfil
         * - ids relacionados
         */
        return new LoginResponse(
                token,
                usuario.getId(),
                usuario.getLogin(),
                usuario.getPerfil(),
                usuario.getStatus(),
                alunaId,
                professoraId,
                nome
        );
    }
}