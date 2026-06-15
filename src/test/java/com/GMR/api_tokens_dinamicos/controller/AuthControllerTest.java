package com.GMR.api_tokens_dinamicos.controller;

import com.GMR.api_tokens_dinamicos.dto.LoginRequestDTO;
import com.GMR.api_tokens_dinamicos.service.AuthService;
import com.GMR.api_tokens_dinamicos.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desliga a barreira padrão do Spring Security para testar a rota pública
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("Deve retornar HTTP 200 (OK) e o JWT em texto puro ao fazer login válido")
    void testLogin_Sucesso() throws Exception {
        String tokenSimulado = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.SimulacaoDeTokenFalso.Assinatura";

        when(authService.autenticarEGerarToken(any(LoginRequestDTO.class)))
                .thenReturn(tokenSimulado);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        // CORREÇÃO: Senha enviada com exatamente 4 números para passar no @Valid
                        .content("{\"agencia\": \"1234\", \"numeroConta\": \"12345-6\", \"senha\": \"1234\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(tokenSimulado));
    }

    @Test
    @DisplayName("Deve retornar HTTP 401 (Unauthorized) e a mensagem exata de erro ao falhar no login")
    void testLogin_FalhaUnauthorized() throws Exception {

        when(authService.autenticarEGerarToken(any(LoginRequestDTO.class)))
                .thenReturn(null);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        // CORREÇÃO: Senha enviada com exatamente 4 números para passar no @Valid
                        .content("{\"agencia\": \"1234\", \"numeroConta\": \"12345-6\", \"senha\": \"9999\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Agência, conta ou senha incorretos. Acesso negado."));
    }
}