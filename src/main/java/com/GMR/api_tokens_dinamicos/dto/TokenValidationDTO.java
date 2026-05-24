package com.GMR.api_tokens_dinamicos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object (DTO) para a requisição de validação de token.
 */
public record TokenValidationDTO(

        @NotBlank(message = "O código do token é obrigatório e não pode estar em branco.")
        @Pattern(regexp = "\\d{6}", message = "O código do token deve conter exatamente 6 dígitos numéricos.")
        String codigo,

        @NotNull(message = "O ID da conta é obrigatório.")
        @Positive(message = "O ID da conta deve ser um número positivo.")
        Long contaId

) {
}