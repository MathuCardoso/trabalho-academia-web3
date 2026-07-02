package br.edu.ifpr.academia.services;

import br.edu.ifpr.academia.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String CHAVE_SECRETA;

    @Value("${jwt.expiration}")
    private long TEMPO_EXPIRACAO;

    private SecretKey getChaveAssinatura() {
        return Keys.hmacShaKeyFor(CHAVE_SECRETA.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(Usuario usuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + TEMPO_EXPIRACAO);

        return Jwts.builder()
                .subject(usuario.getLogin())
                .claim("usuarioId", usuario.getId())
                .claim("perfil", usuario.getPerfil().name())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getChaveAssinatura())
                .compact();
    }

    public String extrairLogin(String token) {
        return extrairClaims(token).getSubject();
    }

    public String extrairPerfil(String token) {
        return extrairClaims(token).get("perfil", String.class);
    }

    public Long extrairUsuarioId(String token) {
        Number usuarioId = extrairClaims(token).get("usuarioId", Number.class);
        return usuarioId.longValue();
    }

    public boolean tokenValido(String token, Usuario usuario) {
        String login = extrairLogin(token);
        return login.equals(usuario.getLogin()) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        Date dataExpiracao = extrairClaims(token).getExpiration();
        return dataExpiracao.before(new Date());
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getChaveAssinatura())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}