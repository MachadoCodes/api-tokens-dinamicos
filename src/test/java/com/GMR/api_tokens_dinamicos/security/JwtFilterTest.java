package com.GMR.api_tokens_dinamicos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @AfterEach
    void tearDown() {
        // Limpa o SecurityContext para um teste não poluir o outro
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve liberar a requisição e autenticar quando o token for válido")
    void testDoFilterInternal_ComTokenValido() throws Exception {
        String token = "TokenFalsoApenasParaSimulacao";

        // Simula a requisição chegando com o Header de autorização
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extrairUsername(token)).thenReturn("12345-6");
        when(jwtUtil.isTokenValido(token, "12345-6")).thenReturn(true);

        // Dispara o filtro
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Verifica se a requisição passou adiante para o Controller
        verify(filterChain, times(1)).doFilter(request, response);

        // Garante que o Spring Security anotou o usuário como logado
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("12345-6", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    @DisplayName("Não deve autenticar quando o cabeçalho Authorization não existir")
    void testDoFilterInternal_SemToken() throws Exception {
        // Simula uma requisição sem JWT
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        // O usuário não pode estar logado
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Não deve autenticar quando o token JWT estiver adulterado ou expirado")
    void testDoFilterInternal_ComTokenInvalido() throws Exception {
        String token = "TokenAdulterado";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extrairUsername(token)).thenReturn("12345-6");
        // Simula que a validação matemática do token falhou
        when(jwtUtil.isTokenValido(token, "12345-6")).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        // O usuário NÃO deve ser logado
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}