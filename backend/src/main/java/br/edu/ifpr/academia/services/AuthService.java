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
 * - verificar status
 * - comparar senha com BCrypt
 * - retornar dados para o front-end
 */
@Service
public class AuthService {

    private final UsuarioService usuarioService;

    public AuthService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /*
     * Realiza login do usuario.
     *
     * O login pode ser:
     * - admin
     * - CPF da aluna
     * - CREF da professora
     */
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioService.buscarPorLogin(request.getLogin());

        if (usuario.getStatus() == StatusCadastro.INATIVO) {
            throw new RuntimeException("Usuario inativo");
        }

        boolean senhaValida = usuarioService.getPasswordEncoder()
                .matches(request.getSenha(), usuario.getSenha());

        if (!senhaValida) {
            throw new RuntimeException("Login ou senha invalidos");
        }

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

        return new LoginResponse(
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