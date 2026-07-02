package br.edu.ifpr.academia.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
 * Classe responsavel por tratar erros da API.
 *
 * Sem essa classe, muitos erros aparecem de forma grande e confusa.
 * Com ela, o front-end recebe respostas JSON mais limpas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Trata erros de validacao do @Valid.
     *
     * Exemplo:
     * - nome vazio
     * - email invalido
     * - cpf vazio
     * - senhaInicial vazia
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarErroDeValidacao(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> erros = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(erro -> {
            erros.put(erro.getField(), erro.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(erros);
    }

    /*
     * Trata erros de permissao do Spring Security.
     *
     * Exemplo:
     * - ALUNA tentando listar todas as alunas
     * - PROFESSORA tentando cadastrar aluna
     * - ALUNA tentando cancelar matricula
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> tratarAcessoNegado(
            AccessDeniedException exception
    ) {
        Map<String, Object> erro = new HashMap<>();

        erro.put("status", HttpStatus.FORBIDDEN.value());
        erro.put("erro", "Acesso negado");
        erro.put("mensagem", "Voce nao tem permissao para acessar este recurso");
        erro.put("dataHora", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    /*
     * Trata erros de regra de negocio lançados com RuntimeException.
     *
     * Exemplo:
     * - CPF ja cadastrado
     * - e-mail ja cadastrado
     * - usuario nao encontrado
     * - login ou senha invalidos
     * - aluna sem matricula ativa
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> tratarRuntimeException(
            RuntimeException exception
    ) {
        Map<String, Object> erro = new HashMap<>();

        erro.put("status", HttpStatus.BAD_REQUEST.value());
        erro.put("erro", "Erro na requisicao");
        erro.put("mensagem", exception.getMessage());
        erro.put("dataHora", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    /*
     * Trata erros de integridade no banco.
     *
     * Exemplo:
     * - tentar cadastrar CPF duplicado
     * - tentar cadastrar e-mail duplicado
     * - violar chave estrangeira
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> tratarErroDeIntegridade(
            DataIntegrityViolationException exception
    ) {
        Map<String, Object> erro = new HashMap<>();

        erro.put("status", HttpStatus.CONFLICT.value());
        erro.put("erro", "Erro de integridade no banco de dados");
        erro.put("mensagem", "Registro duplicado ou vinculado a outro cadastro");
        erro.put("dataHora", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    /*
     * Trata erros inesperados.
     *
     * Evita que o sistema devolva stack trace completo para o front.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> tratarErroGenerico(
            Exception exception
    ) {
        Map<String, Object> erro = new HashMap<>();

        erro.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        erro.put("erro", "Erro interno no servidor");
        erro.put("mensagem", "Ocorreu um erro inesperado");
        erro.put("dataHora", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}