package br.edu.ifpr.academia.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Classe responsavel por tratar erros da API.
 *
 * Sem essa classe, muitos erros aparecem de forma grande e confusa.
 * Com ela, o front-end recebe respostas JSON mais limpas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> criarErro(
            HttpStatus status,
            String erro,
            String mensagem,
            Map<String, String> errors
    ) {
        Map<String, Object> resposta = new LinkedHashMap<>();

        resposta.put("status", status.value());
        resposta.put("erro", erro);
        resposta.put("mensagem", mensagem);
        resposta.put("errors", errors == null ? Map.of() : errors);
        resposta.put("dataHora", LocalDateTime.now());

        return resposta;
    }

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
    public ResponseEntity<Map<String, Object>> tratarErroDeValidacao(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> erros = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(erro -> {
            erros.put(erro.getField(), erro.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(
                criarErro(
                        HttpStatus.BAD_REQUEST,
                        "Erro de validacao",
                        "Dados invalidos",
                        erros
                )
        );
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> tratarApiException(
            ApiException exception
    ) {
        return ResponseEntity.status(exception.getStatus()).body(
                criarErro(
                        exception.getStatus(),
                        exception.getErro(),
                        exception.getMessage(),
                        exception.getErrors()
                )
        );
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
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                criarErro(
                        HttpStatus.FORBIDDEN,
                        "Acesso negado",
                        "Voce nao tem permissao para acessar este recurso",
                        Map.of()
                )
        );
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Map<String, Object>> tratarRequisicaoInvalida(
            Exception exception
    ) {
        return ResponseEntity.badRequest().body(
                criarErro(
                        HttpStatus.BAD_REQUEST,
                        "Erro na requisicao",
                        "Corpo da requisicao invalido",
                        Map.of()
                )
        );
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
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                criarErro(
                        HttpStatus.CONFLICT,
                        "Erro de integridade no banco de dados",
                        "Registro duplicado ou vinculado a outro cadastro",
                        Map.of()
                )
        );
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
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                criarErro(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro interno no servidor",
                        "Ocorreu um erro inesperado",
                        Map.of()
                )
        );
    }
}
