package br.edu.ifpr.academia.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Map;

/*
 * Excecao de negocio da API.
 *
 * Permite que os services indiquem o codigo HTTP correto
 * e, quando fizer sentido, o campo que causou o erro.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String erro;
    private final Map<String, String> errors;

    public ApiException(HttpStatus status, String mensagem) {
        this(status, descricao(status), mensagem, Collections.emptyMap());
    }

    public ApiException(HttpStatus status, String mensagem, String campo) {
        this(status, descricao(status), mensagem, Map.of(campo, mensagem));
    }

    public ApiException(
            HttpStatus status,
            String erro,
            String mensagem,
            Map<String, String> errors
    ) {
        super(mensagem);
        this.status = status;
        this.erro = erro;
        this.errors = errors == null ? Collections.emptyMap() : errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErro() {
        return erro;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    private static String descricao(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "Erro na requisicao";
            case UNAUTHORIZED -> "Nao autenticado";
            case FORBIDDEN -> "Acesso negado";
            case NOT_FOUND -> "Recurso nao encontrado";
            case CONFLICT -> "Conflito";
            default -> "Erro na API";
        };
    }
}
