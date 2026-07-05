package br.edu.ifpr.academia.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .cors(Customizer.withDefaults())

                                .csrf(AbstractHttpConfigurer::disable)

                                .formLogin(AbstractHttpConfigurer::disable)

                                .httpBasic(AbstractHttpConfigurer::disable)

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/login").permitAll()
                                                .anyRequest().authenticated())

                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint((request, response, exception) ->
                                                                escreverErro(
                                                                                response,
                                                                                HttpStatus.UNAUTHORIZED,
                                                                                "Nao autenticado",
                                                                                "Token de autenticacao ausente ou invalido"))
                                                .accessDeniedHandler((request, response, exception) ->
                                                                escreverErro(
                                                                                response,
                                                                                HttpStatus.FORBIDDEN,
                                                                                "Acesso negado",
                                                                                "Voce nao tem permissao para acessar este recurso")))

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                .build();
        }

        @Bean
        WebMvcConfigurer configurarCors() {
                return new WebMvcConfigurer() {
                        @Override
                        public void addCorsMappings(CorsRegistry registry) {
                                registry.addMapping("/api/**")
                                                .allowedOriginPatterns(
                                                                "http://localhost:*",
                                                                "http://127.0.0.1:*")
                                                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                                                .allowedHeaders("*")
                                                .exposedHeaders("Authorization");
                        }
                };
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
