package br.edu.ifpr.academia.config;

import br.edu.ifpr.academia.entities.Usuario;
import br.edu.ifpr.academia.enums.StatusCadastro;
import br.edu.ifpr.academia.services.JwtService;
import br.edu.ifpr.academia.services.UsuarioDetailsService;
import br.edu.ifpr.academia.services.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

/*
 * Filtro JWT da aplicação.
 *
 * Esse filtro roda antes das requisições chegarem nos controllers.
 *
 * Ele verifica se a requisição possui o header:
 *
 * Authorization: Bearer TOKEN
 *
 * Se o token for válido, o filtro autentica o usuário dentro do Spring Security.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final UsuarioDetailsService usuarioDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioService usuarioService,
            UsuarioDetailsService usuarioDetailsService
    ) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.usuarioDetailsService = usuarioDetailsService;
    }

    /*
     * Método executado automaticamente em cada requisição HTTP.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * Lê o header Authorization.
         *
         * Exemplo esperado:
         * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
         */
        String authorizationHeader = request.getHeader("Authorization");

        /*
         * Se não tiver Authorization ou se não começar com Bearer,
         * o filtro não tenta autenticar ninguém.
         *
         * A requisição continua normalmente.
         * Depois, o SecurityConfig decide se aquela rota é pública ou protegida.
         */
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Remove o prefixo "Bearer " e fica apenas com o token.
         */
        String token = authorizationHeader.substring(7);

        try {
            /*
             * Extrai o login de dentro do token.
             */
            String login = jwtService.extrairLogin(token);

            /*
             * Só autentica se:
             * - o login existe no token
             * - ainda não existe usuário autenticado no contexto atual
             */
            if (login != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                /*
                 * Busca o Usuario real do sistema.
                 *
                 * Aqui usamos nossa entidade Usuario para validar status,
                 * login e token.
                 */
                Usuario usuario = usuarioService.buscarPorLogin(login);

                if (usuario.getStatus() == StatusCadastro.INATIVO) {
                    escreverErro(
                            response,
                            HttpStatus.UNAUTHORIZED,
                            "Nao autenticado",
                            "Usuario inativo"
                    );
                    return;
                }

                /*
                 * Valida se o token pertence ao usuário e se não expirou.
                 */
                if (jwtService.tokenValido(token, usuario)) {

                    /*
                     * Carrega o usuário no formato que o Spring Security entende.
                     *
                     * Aqui entram:
                     * - username
                     * - password
                     * - authorities, como ROLE_ADMIN, ROLE_ALUNA, ROLE_PROFESSORA
                     */
                    UserDetails userDetails = usuarioDetailsService.loadUserByUsername(login);

                    /*
                     * Cria o objeto de autenticação do Spring Security.
                     *
                     * Como estamos usando JWT, não precisamos colocar senha aqui.
                     */
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * Adiciona detalhes da requisição, como IP e sessão.
                     */
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    /*
                     * Marca o usuário como autenticado no contexto do Spring Security.
                     *
                     * A partir daqui, o Spring consegue saber quem é o usuário
                     * e quais permissões ele tem.
                     */
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (RuntimeException exception) {
            escreverErro(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    "Nao autenticado",
                    "Token de autenticacao invalido ou expirado"
            );
            return;
        }

        /*
         * Continua o fluxo da requisição.
         */
        filterChain.doFilter(request, response);
    }

    private void escreverErro(
            HttpServletResponse response,
            HttpStatus status,
            String erro,
            String mensagem
    ) throws IOException {
        String body = """
                {"status":%d,"erro":"%s","mensagem":"%s","errors":{},"dataHora":"%s"}
                """.formatted(
                status.value(),
                escaparJson(erro),
                escaparJson(mensagem),
                LocalDateTime.now()
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(body);
    }

    private String escaparJson(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
