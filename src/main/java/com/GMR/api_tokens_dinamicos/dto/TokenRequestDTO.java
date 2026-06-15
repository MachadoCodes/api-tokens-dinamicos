package com.GMR.api_tokens_dinamicos.dto;

import com.GMR.api_tokens_dinamicos.model.Token.TipoComunicacao;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) para a requisição de geração de token.
 * O ID da conta e o destino (telefone/email) são extraídos de forma segura
 * via JWT e Banco de Dados no Controller, garantindo o princípio de Zero-Trust.
 */
public record TokenRequestDTO(

        @NotNull(message = "O tipo de comunicação (SMS, EMAIL ou LIGACAO) é obrigatório.")
        TipoComunicacao tipo

) {
}