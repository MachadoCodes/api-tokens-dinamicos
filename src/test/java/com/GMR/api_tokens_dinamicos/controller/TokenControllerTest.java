package com.GMR.api_tokens_dinamicos.controller;

import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Token;
import com.GMR.api_tokens_dinamicos.model.Usuario;
import com.GMR.api_tokens_dinamicos.service.TokenService;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import com.GMR.api_tokens_dinamicos.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TokenController.class)
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private ContaRepository contaRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    // ==========================================
    // TESTES DA ROTA: /validar
    // ==========================================

    @Test
    @WithMockUser(username = "12345-6")
    @DisplayName("Deve retornar HTTP 200 (OK) ao receber um código JSON válido")
    void testValidarToken_RetornaHttp200() throws Exception {
        Token tokenValido = new Token();
        tokenValido.setTipoComunicacao(Token.TipoComunicacao.SMS);

        when(tokenService.validarTokenSeguro(eq("999999"), anyString())).thenReturn(tokenValido);

        mockMvc.perform(post("/api/v1/tokens/validar").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\": \"999999\", \"contaId\": 10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Autenticidade confirmada! A comunicação recebida é legítima e foi enviada por nossa instituição."))
                .andExpect(jsonPath("$.tipoCanal").value("SMS"));
    }

    @Test
    @WithMockUser(username = "12345-6")
    @DisplayName("Deve retornar HTTP 400 (Bad Request) ao receber um código fraudulento")
    void testValidarToken_RetornaHttp400_Fraude() throws Exception {
        when(tokenService.validarTokenSeguro(eq("111111"), anyString()))
                .thenThrow(new IllegalArgumentException("Tentativa de golpe bloqueada."));

        mockMvc.perform(post("/api/v1/tokens/validar").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\": \"111111\", \"contaId\": 10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Tentativa de golpe bloqueada."));
    }

    @Test
    @WithMockUser(username = "12345-6")
    @DisplayName("Deve retornar HTTP 400 (Bad Request) se o token já estiver expirado")
    void testValidarToken_RetornaHttp400_Expirado() throws Exception {
        when(tokenService.validarTokenSeguro(eq("888888"), anyString()))
                .thenThrow(new IllegalArgumentException("O tempo de validade do Token expirou."));

        mockMvc.perform(post("/api/v1/tokens/validar").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\": \"888888\", \"contaId\": 10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O tempo de validade do Token expirou."));
    }

    // ==========================================
    // TESTES DA ROTA: /gerar
    // ==========================================

    @Test
    @WithMockUser(username = "12345-6")
    @DisplayName("Deve retornar HTTP 201 (Created) ao gerar um novo token via SMS")
    void testGerarToken_RetornaHttp201() throws Exception {
        // Prepara os dados simulados
        Usuario usuario = new Usuario();
        usuario.setTelefoneCelular("11988887777");

        Conta conta = new Conta();
        conta.setUsuario(usuario);

        Token tokenGerado = new Token();
        tokenGerado.setCodigo("123456");
        tokenGerado.setTipoComunicacao(Token.TipoComunicacao.SMS);

        // Instrui os mocks
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.of(conta));
        when(tokenService.gerarTokenParaComunicacao(eq(conta), eq("+5511988887777"), eq(Token.TipoComunicacao.SMS)))
                .thenReturn(tokenGerado);

        // Dispara a requisição
        mockMvc.perform(post("/api/v1/tokens/gerar").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\": \"SMS\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("123456"))
                .andExpect(jsonPath("$.tipoComunicacao").value("SMS"));
    }

    @Test
    @WithMockUser(username = "12345-6")
    @DisplayName("Deve retornar HTTP 401 (Unauthorized) ao tentar gerar token para uma conta inexistente")
    void testGerarToken_RetornaHttp401_ContaNaoEncontrada() throws Exception {
        // Se a conta não existe no banco
        when(contaRepository.findByNumeroConta("12345-6")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/v1/tokens/gerar").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\": \"EMAIL\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ==========================================
    // TESTES DA ROTA: /historico
    // ==========================================

    @Test
    @WithMockUser(username = "12345-6")
    @DisplayName("Deve retornar HTTP 200 (OK) e a lista de histórico de tokens")
    void testListarHistorico_RetornaHttp200() throws Exception {
        Token tokenHistorico = new Token();
        tokenHistorico.setCodigo("654321");
        tokenHistorico.setStatus(Token.StatusToken.USADO);
        tokenHistorico.setTipoComunicacao(Token.TipoComunicacao.EMAIL);
        tokenHistorico.setDataExpiracao(LocalDateTime.now().plusMinutes(10));

        when(tokenService.buscarHistorico90Dias("12345-6")).thenReturn(List.of(tokenHistorico));

        mockMvc.perform(get("/api/v1/tokens/historico")) // GET não exige CSRF
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("654321"))
                .andExpect(jsonPath("$[0].canal").value("EMAIL"))
                .andExpect(jsonPath("$[0].status").value("USADO"));
    }
}