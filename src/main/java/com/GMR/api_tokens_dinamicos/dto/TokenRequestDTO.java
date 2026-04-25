package com.GMR.api_tokens_dinamicos.dto;

import com.GMR.api_tokens_dinamicos.model.Token.TipoComunicacao;

/**
 * Data Transfer Object (DTO) para a requisição de geração de token.
 * Utiliza o recurso Record do Java para garantir dados imutáveis na entrada da API.
 */
public record TokenRequestDTO(Long contaId, String destino, TipoComunicacao tipo) {

}
