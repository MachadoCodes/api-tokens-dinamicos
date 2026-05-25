package com.GMR.api_tokens_dinamicos.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que padroniza o formato de resposta da API quando ocorre um erro.
 */
public record StandardErrorDTO(
        LocalDateTime timestamp,
        Integer status,
        String erro,
        List<String> mensagens
) {
}