package com.GMR.api_tokens_dinamicos.dto;

import com.GMR.api_tokens_dinamicos.model.Token.TipoComunicacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) para a requisição de geração de token.
 * Utiliza o recurso Record do Java para garantir dados imutáveis na entrada da API.
 */
public record TokenRequestDTO(

        @NotNull(message = "O ID da conta é obrigatório.")
        @Positive(message = "O ID da conta deve ser um número positivo.")
        Long contaId,

        @NotBlank(message = "O destino (e-mail ou telefone) é obrigatório e não pode estar em branco.")
        @Size(min = 8, max = 255, message = "O destino deve conter entre 8 e 255 caracteres.")
        String destino,

        @NotNull(message = "O tipo de comunicação (SMS, EMAIL ou LIGACAO) é obrigatório.")
        TipoComunicacao tipo

) {
}