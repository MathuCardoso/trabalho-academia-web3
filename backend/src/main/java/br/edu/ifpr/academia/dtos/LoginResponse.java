package br.edu.ifpr.academia.dtos;

import br.edu.ifpr.academia.enums.PerfilUsuario;
import br.edu.ifpr.academia.enums.StatusCadastro;

/*
 * DTO usado para devolver os dados do usuario logado.
 *
 * Agora, com JWT, esse DTO tambem devolve o token.
 *
 * Importante:
 * Nunca retornar a senha nesse DTO.
 * Nem senha pura, nem senha criptografada.
 */
public class LoginResponse {

    /*
     * Token JWT gerado pelo back-end.
     *
     * O front-end devera guardar esse token e enviar nas proximas requisicoes
     * usando o header:
     *
     * Authorization: Bearer TOKEN
     */
    private String token;

    /*
     * ID do Usuario que fez login.
     */
    private Long usuarioId;

    /*
     * Login usado pelo usuario.
     *
     * Pode ser:
     * - admin
     * - CPF da aluna
     * - CREF da professora
     */
    private String login;

    /*
     * Perfil de acesso do usuario.
     *
     * Pode ser:
     * - ADMIN
     * - ALUNA
     * - PROFESSORA
     */
    private PerfilUsuario perfil;

    /*
     * Status de acesso do Usuario.
     *
     * Se estiver INATIVO, o usuario nao deve conseguir logar.
     */
    private StatusCadastro status;

    /*
     * Se o usuario for ALUNA, esse campo vem preenchido.
     *
     * Se for ADMIN ou PROFESSORA, fica null.
     */
    private Long alunaId;

    /*
     * Se o usuario for PROFESSORA, esse campo vem preenchido.
     *
     * Se for ADMIN ou ALUNA, fica null.
     */
    private Long professoraId;

    /*
     * Nome exibido no front-end.
     *
     * Para ADMIN: Administrador
     * Para ALUNA: nome da aluna
     * Para PROFESSORA: nome da professora
     */
    private String nome;

    public LoginResponse() {
    }

    /*
     * Construtor antigo mantido temporariamente para evitar quebrar o AuthService
     * antes de alterarmos ele no proximo passo.
     *
     * Quando o AuthService for ajustado, usaremos o construtor com token.
     */
    public LoginResponse(
            Long usuarioId,
            String login,
            PerfilUsuario perfil,
            StatusCadastro status,
            Long alunaId,
            Long professoraId,
            String nome
    ) {
        this.usuarioId = usuarioId;
        this.login = login;
        this.perfil = perfil;
        this.status = status;
        this.alunaId = alunaId;
        this.professoraId = professoraId;
        this.nome = nome;
    }

    /*
     * Novo construtor com token JWT.
     */
    public LoginResponse(
            String token,
            Long usuarioId,
            String login,
            PerfilUsuario perfil,
            StatusCadastro status,
            Long alunaId,
            Long professoraId,
            String nome
    ) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.login = login;
        this.perfil = perfil;
        this.status = status;
        this.alunaId = alunaId;
        this.professoraId = professoraId;
        this.nome = nome;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }


    public StatusCadastro getStatus() {
        return status;
    }

    public void setStatus(StatusCadastro status) {
        this.status = status;
    }


    public Long getAlunaId() {
        return alunaId;
    }

    public void setAlunaId(Long alunaId) {
        this.alunaId = alunaId;
    }


    public Long getProfessoraId() {
        return professoraId;
    }

    public void setProfessoraId(Long professoraId) {
        this.professoraId = professoraId;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}