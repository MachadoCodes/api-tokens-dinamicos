package com.GMR.api_tokens_dinamicos.dto;

/**
 * Data Transfer Object (DTO) para a requisição de validação de token.
 */
public record TokenValidationDTO(String codigo, Long contaId) {

}
