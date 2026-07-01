package br.edu.ifpr.academia.dtos;

import br.edu.ifpr.academia.enums.PerfilUsuario;
import br.edu.ifpr.academia.enums.StatusCadastro;

/*
 * DTO usado para devolver os dados do usuario logado.
 *
 * Nunca retornar senha nesse DTO.
 */
public class LoginResponse {

    private Long usuarioId;
    private String login;
    private PerfilUsuario perfil;
    private StatusCadastro status;
    private Long alunaId;
    private Long professoraId;
    private String nome;

    public LoginResponse() {
    }

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