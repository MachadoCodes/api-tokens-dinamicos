package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.model.Token;

/**
 * Interface que define o contrato para o envio de comunicações.
 * Facilita o desacoplamento: permite trocar a implementação (Mock por uma API real como AWS) sem alterar a regra de negócio.
 */
public interface MensageriaService {
    void enviarComunicacao(String destino, String token, Token.TipoComunicacao tipo);
}
