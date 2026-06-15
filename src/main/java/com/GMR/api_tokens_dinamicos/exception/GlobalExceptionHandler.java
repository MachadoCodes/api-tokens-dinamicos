package com.GMR.api_tokens_dinamicos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// 👇 Importação do DTO que está na camada correta
import com.GMR.api_tokens_dinamicos.dto.StandardErrorDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Intercepta erros gerados pelas anotações do Bean Validation (@Valid, @NotBlank, etc.)
     * Retorna HTTP 400 (Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Pega todos os erros de campo e cria uma lista de mensagens limpas
        List<String> errosDeValidacao = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        StandardErrorDTO errorResponse = new StandardErrorDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Falha na validação dos dados enviados.",
                errosDeValidacao
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Intercepta as ResponseStatusException que já utilizamos nos Services (ex: Erro 404)
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<StandardErrorDTO> handleResponseStatusException(ResponseStatusException ex) {
        StandardErrorDTO errorResponse = new StandardErrorDTO(
                LocalDateTime.now(),
                ex.getStatusCode().value(),
                "Erro na requisição",
                List.of(ex.getReason())
        );

        return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
    }

    /**
     * Intercepta qualquer outro erro inesperado no sistema.
     * Retorna HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardErrorDTO> handleGenericException(Exception ex) {
        StandardErrorDTO errorResponse = new StandardErrorDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno no servidor",
                List.of("Ocorreu um erro inesperado. Por favor, contate o suporte da instituição.")
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}