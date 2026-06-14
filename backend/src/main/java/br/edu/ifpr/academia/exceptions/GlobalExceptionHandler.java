package br.edu.ifpr.academia.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
     * Nota:
     * Este método trata erros de validação gerados pelo @Valid.
     *
     * Exemplo:
     * Se o usuário tentar cadastrar uma aluna com nome vazio
     * ou e-mail inválido, o Spring dispara uma exceção do tipo
     * MethodArgumentNotValidException.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarErrosDeValidacao(MethodArgumentNotValidException exception) {

        /*
         * Nota:
         * Aqui criamos um Map para guardar os erros.
         *
         * A chave será o nome do campo.
         * O valor será a mensagem de erro.
         *
         * Exemplo:
         * "nome" -> "O nome é obrigatório"
         * "email" -> "Informe um e-mail válido"
         */
        Map<String, String> erros = new HashMap<>();

        /*
         * Nota:
         * Aqui pegamos todos os campos que falharam na validação.
         *
         * Para cada erro encontrado:
         * - erro.getField() pega o nome do campo
         * - erro.getDefaultMessage() pega a mensagem definida na entidade
         */
        exception.getBindingResult().getFieldErrors().forEach(erro -> {
            erros.put(erro.getField(), erro.getDefaultMessage());
        });

        /*
         * Nota:
         * Retornamos status 400 Bad Request,
         * indicando que o cliente enviou dados inválidos.
         *
         * O corpo da resposta será apenas o Map com os erros,
         * sem aquele trace gigante que apareceu antes.
         */
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }
}