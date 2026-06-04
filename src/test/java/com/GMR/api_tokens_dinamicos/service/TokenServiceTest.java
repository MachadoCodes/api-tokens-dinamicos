package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.model.Comunicacao;
import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Token;
import com.GMR.api_tokens_dinamicos.repository.ComunicacaoRepository;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import com.GMR.api_tokens_dinamicos.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private ContaRepository contaRepository;
    @Mock
    private ComunicacaoRepository comunicacaoRepository;
    @Mock
    private SmsService smsService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private TokenService tokenService;

    private Conta contaMock;
    private Token tokenMock;

    @BeforeEach
    void setUp() {
        contaMock = new Conta();
        contaMock.setId(1L);
        contaMock.setNumeroConta("12345-6");

        tokenMock = new Token();
        tokenMock.setCodigo("999999");
        tokenMock.setConta(contaMock);
        tokenMock.setDataExpiracao(LocalDateTime.now().plusMinutes(10));
    }

    // ==========================================
    // TESTES DE VALIDAÇÃO E SEGURANÇA (ANTIFRAUDE)
    // ==========================================

    @Test
    @DisplayName("Deve estourar erro se a conta for inválida ao validar o token")
    void testValidarToken_ContaInvalida() {
        when(contaRepository.findByNumeroConta("00000-0")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            tokenService.validarTokenSeguro("999999", "00000-0");
        });
    }

    @Test
    @DisplayName("Deve registrar um token SUSPEITO se o código não for encontrado (1ª tentativa de fraude)")
    void testValidarToken_TokenNaoEncontrado_PrimeiraFraude() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(contaMock));
        when(tokenRepository.findByCodigoAndContaId("000000", 1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenService.validarTokenSeguro("000000", "12345-6");
        });

        // CORREÇÃO: Procurando pela exata frase do início da sua exceção
        assertTrue(exception.getMessage().contains("Tentativa de golpe"));

        // Verifica se o sistema salvou um token suspeito para auditoria
        verify(tokenRepository, times(1)).save(argThat(t -> t.getStatus() == Token.StatusToken.SUSPEITO));
    }

    @Test
    @DisplayName("Deve barrar a re-validação de um token previamente marcado como SUSPEITO")
    void testValidarToken_BloqueioDeFraudeRepetida() {
        tokenMock.setStatus(Token.StatusToken.SUSPEITO);

        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(contaMock));
        when(tokenRepository.findByCodigoAndContaId("999999", 1L)).thenReturn(Optional.of(tokenMock));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenService.validarTokenSeguro("999999", "12345-6");
        });

        assertTrue(exception.getMessage().contains("Tentativa de golpe bloqueada"));
    }

    @Test
    @DisplayName("Deve barrar validação de um token já marcado como USADO")
    void testValidarToken_TokenJaUsado() {
        tokenMock.setStatus(Token.StatusToken.USADO);
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(contaMock));
        when(tokenRepository.findByCodigoAndContaId("999999", 1L)).thenReturn(Optional.of(tokenMock));

        assertThrows(IllegalArgumentException.class, () -> {
            tokenService.validarTokenSeguro("999999", "12345-6");
        });
    }

    @Test
    @DisplayName("Deve barrar validação de um token marcado como EXPIRADO")
    void testValidarToken_TokenStatusExpirado() {
        tokenMock.setStatus(Token.StatusToken.EXPIRADO);
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(contaMock));
        when(tokenRepository.findByCodigoAndContaId("999999", 1L)).thenReturn(Optional.of(tokenMock));

        assertThrows(IllegalArgumentException.class, () -> {
            tokenService.validarTokenSeguro("999999", "12345-6");
        });
    }

    @Test
    @DisplayName("Deve barrar a validação de um token cujo tempo acabou de expirar")
    void testValidarToken_BloqueioPorExpiracaoDeTempo() {
        tokenMock.setStatus(Token.StatusToken.ATIVO);
        tokenMock.setDataExpiracao(LocalDateTime.now().minusMinutes(5));

        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(contaMock));
        when(tokenRepository.findByCodigoAndContaId("999999", 1L)).thenReturn(Optional.of(tokenMock));

        assertThrows(IllegalArgumentException.class, () -> {
            tokenService.validarTokenSeguro("999999", "12345-6");
        });

        assertEquals(Token.StatusToken.EXPIRADO, tokenMock.getStatus());
    }

    @Test
    @DisplayName("Deve validar com sucesso um token ATIVO e dentro da validade")
    void testValidarToken_SucessoCaminhoFeliz() {
        tokenMock.setStatus(Token.StatusToken.ATIVO);

        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(contaMock));
        when(tokenRepository.findByCodigoAndContaId("999999", 1L)).thenReturn(Optional.of(tokenMock));

        Token tokenValidado = tokenService.validarTokenSeguro("999999", "12345-6");

        assertNotNull(tokenValidado);
        assertEquals(Token.StatusToken.USADO, tokenValidado.getStatus());
    }

    // ==========================================
    // TESTES DE GERAÇÃO DE TOKENS E COMUNICAÇÃO
    // ==========================================

    @Test
    @DisplayName("Deve gerar um token para SMS e salvar o histórico de comunicação com sucesso")
    void testGerarTokenParaComunicacao_SmsSucesso() {
        Token tokenGerado = tokenService.gerarTokenParaComunicacao(contaMock, "+5511999999999", Token.TipoComunicacao.SMS);
        assertEquals(Token.TipoComunicacao.SMS, tokenGerado.getTipoComunicacao());
        verify(smsService, times(1)).enviarSms(eq("+5511999999999"), anyString());
    }

    @Test
    @DisplayName("Deve gerar um token para EMAIL e salvar o histórico de comunicação com sucesso")
    void testGerarTokenParaComunicacao_EmailSucesso() throws Exception { // <-- Só adicionar o 'throws Exception' aqui!
        Token tokenGerado = tokenService.gerarTokenParaComunicacao(contaMock, "teste@teste.com", Token.TipoComunicacao.EMAIL);
        assertEquals(Token.TipoComunicacao.EMAIL, tokenGerado.getTipoComunicacao());
        verify(emailService, times(1)).enviarEmail(eq("teste@teste.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve gerar um token para LIGACAO simulando o áudio")
    void testGerarTokenParaComunicacao_LigacaoSucesso() {
        Token tokenGerado = tokenService.gerarTokenParaComunicacao(contaMock, "11999999999", Token.TipoComunicacao.LIGACAO);
        assertEquals(Token.TipoComunicacao.LIGACAO, tokenGerado.getTipoComunicacao());
    }

    @Test
    @DisplayName("Deve passar pelo switch default caso o canal seja DESCONHECIDO")
    void testGerarTokenParaComunicacao_CanalDesconhecido() {
        Token tokenGerado = tokenService.gerarTokenParaComunicacao(contaMock, "000", Token.TipoComunicacao.DESCONHECIDO);
        assertEquals(Token.TipoComunicacao.DESCONHECIDO, tokenGerado.getTipoComunicacao());
    }

    @Test
    @DisplayName("Não deve interromper a geração do token se o serviço de e-mail falhar (Mock de Emergência)")
    void testGerarTokenParaComunicacao_FalhaNaIntegracao() throws Exception {
        doThrow(new RuntimeException("Falha no e-mail")).when(emailService).enviarEmail(anyString(), anyString(), anyString());
        Token tokenGerado = tokenService.gerarTokenParaComunicacao(contaMock, "cliente@teste.com", Token.TipoComunicacao.EMAIL);
        assertNotNull(tokenGerado);
    }

    // ==========================================
    // TESTES DO HISTÓRICO
    // ==========================================

    @Test
    @DisplayName("Deve buscar histórico de 90 dias com sucesso quando a conta é localizada")
    void testBuscarHistorico90Dias_ContaEncontrada() {
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(contaMock));
        when(tokenRepository.findByContaIdAndDataExpiracaoAfterOrderByDataExpiracaoDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(java.util.List.of(tokenMock));

        java.util.List<Token> historico = tokenService.buscarHistorico90Dias("12345-6");
        assertFalse(historico.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia no histórico se o número da conta não existir no banco")
    void testBuscarHistorico90Dias_ContaNaoEncontrada() {
        when(contaRepository.findByNumeroConta("00000-0")).thenReturn(Optional.empty());
        java.util.List<Token> historico = tokenService.buscarHistorico90Dias("00000-0");
        assertTrue(historico.isEmpty());
    }
}