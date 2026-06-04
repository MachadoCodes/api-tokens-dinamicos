package com.GMR.api_tokens_dinamicos.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain; // <-- Importação adicionada
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtFilter jwtFilter;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    @DisplayName("Deve configurar e instanciar o SecurityFilterChain corretamente")
    void testSecurityFilterChain() throws Exception {
        // Arrange: Mockamos a classe HttpSecurity do Spring
        HttpSecurity httpSecurityMock = mock(HttpSecurity.class);

        // CORREÇÃO: Mockando a classe concreta que o build() espera retornar
        DefaultSecurityFilterChain chainMock = mock(DefaultSecurityFilterChain.class);

        // Simulamos o padrão "Builder"
        when(httpSecurityMock.cors(any())).thenReturn(httpSecurityMock);
        when(httpSecurityMock.csrf(any())).thenReturn(httpSecurityMock);
        when(httpSecurityMock.authorizeHttpRequests(any())).thenReturn(httpSecurityMock);
        when(httpSecurityMock.sessionManagement(any())).thenReturn(httpSecurityMock);
        when(httpSecurityMock.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class))).thenReturn(httpSecurityMock);

        // Agora o compilador aceita perfeitamente!
        when(httpSecurityMock.build()).thenReturn(chainMock);

        // Act: Chamamos o seu método passando o Mock
        SecurityFilterChain result = securityConfig.securityFilterChain(httpSecurityMock);

        // Assert: Garantimos que o filtro de segurança foi construído e retornado
        assertNotNull(result, "O SecurityFilterChain não deve ser nulo");
    }

    @Test
    @DisplayName("Deve instanciar o AuthenticationManager")
    void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration configMock = mock(AuthenticationConfiguration.class);
        AuthenticationManager managerMock = mock(AuthenticationManager.class);
        when(configMock.getAuthenticationManager()).thenReturn(managerMock);

        AuthenticationManager result = securityConfig.authenticationManager(configMock);

        assertNotNull(result, "O AuthenticationManager não deve ser nulo");
    }

    @Test
    @DisplayName("Deve instanciar o UserDetailsService do sistema em memória")
    void testUserDetailsService() {
        UserDetailsService result = securityConfig.userDetailsService();
        assertNotNull(result, "O UserDetailsService não deve ser nulo");
    }

    @Test
    @DisplayName("Deve instanciar o PasswordEncoder (BCrypt)")
    void testPasswordEncoder() {
        PasswordEncoder result = securityConfig.passwordEncoder();
        assertNotNull(result, "O PasswordEncoder não deve ser nulo");
    }
}