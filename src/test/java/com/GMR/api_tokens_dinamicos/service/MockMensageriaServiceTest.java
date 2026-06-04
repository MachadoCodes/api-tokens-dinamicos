package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.model.Token;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MockMensageriaServiceTest {

    // Como não há dependências injetadas, testamos a classe pura
    private final MockMensageriaService mockMensageriaService = new MockMensageriaService();

    @Test
    @DisplayName("Deve executar a impressão do cenário de SMS")
    void testEnviarComunicacao_Sms() {
        mockMensageriaService.enviarComunicacao("11999999999", "123456", Token.TipoComunicacao.SMS);
    }

    @Test
    @DisplayName("Deve executar a impressão do cenário de EMAIL")
    void testEnviarComunicacao_Email() {
        mockMensageriaService.enviarComunicacao("teste@teste.com", "123456", Token.TipoComunicacao.EMAIL);
    }

    @Test
    @DisplayName("Deve executar a impressão do cenário de LIGACAO")
    void testEnviarComunicacao_Ligacao() {
        mockMensageriaService.enviarComunicacao("11999999999", "123456", Token.TipoComunicacao.LIGACAO);
    }
}