package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.dto.LoginRequestDTO;
import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Credencial;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import com.GMR.api_tokens_dinamicos.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Conta contaMock;
    private Credencial credencialMock;
    private LoginRequestDTO loginDTO;

    @BeforeEach
    void setUp() {
        // 1. Preparamos a Conta falsa
        contaMock = new Conta();
        contaMock.setId(1L);
        contaMock.setAgencia("1234");
        contaMock.setNumeroConta("12345-6");

        // 2. Preparamos a Credencial falsa simulando o que viria da base de dados
        credencialMock = new Credencial();
        credencialMock.setHashSenha("hash_criptografado_da_senha");
        contaMock.setCredencial(credencialMock);

        // 3. Preparamos o DTO que o utilizador enviaria via POST no frontend
        loginDTO = new LoginRequestDTO("1234", "12345-6", "senhaLimpa123");
    }

    @Test
    @DisplayName("Deve validar credenciais e retornar o JWT em caso de sucesso")
    void testAutenticarEGerarToken_Sucesso() {
        // Arrange: Ensinamos o Mockito a simular o caminho feliz
        when(contaRepository.findByAgenciaAndNumeroConta("1234", "12345-6"))
                .thenReturn(Optional.of(contaMock));
        when(passwordEncoder.matches("senhaLimpa123", "hash_criptografado_da_senha"))
                .thenReturn(true);
        when(jwtUtil.gerarToken(contaMock))
                .thenReturn("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        // Act
        String token = authService.autenticarEGerarToken(loginDTO);

        // Assert: Garantimos que o JWT foi devolvido
        assertNotNull(token, "O token não pode ser nulo se as credenciais estiverem corretas.");
        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", token);
        verify(jwtUtil, times(1)).gerarToken(contaMock);
    }

    @Test
    @DisplayName("Deve retornar null ao tentar autenticar com senha incorreta")
    void testAutenticarEGerarToken_SenhaIncorreta() {
        // Arrange: A conta existe, mas o PasswordEncoder dirá que a senha não bate
        when(contaRepository.findByAgenciaAndNumeroConta("1234", "12345-6"))
                .thenReturn(Optional.of(contaMock));
        when(passwordEncoder.matches("senhaLimpa123", "hash_criptografado_da_senha"))
                .thenReturn(false);

        // Act
        String token = authService.autenticarEGerarToken(loginDTO);

        // Assert: O serviço deve blindar o acesso retornando null
        assertNull(token, "O token deve ser nulo caso a senha seja inválida.");
        verify(jwtUtil, never()).gerarToken(any(Conta.class)); // O gerador de JWT nunca pode ser chamado
    }

    @Test
    @DisplayName("Deve retornar null ao tentar autenticar numa conta inexistente")
    void testAutenticarEGerarToken_ContaInexistente() {
        // Arrange: Simula uma resposta vazia do banco de dados (Optional.empty)
        when(contaRepository.findByAgenciaAndNumeroConta("1234", "12345-6"))
                .thenReturn(Optional.empty());

        // Act
        String token = authService.autenticarEGerarToken(loginDTO);

        // Assert:
        assertNull(token, "O token deve ser nulo caso a conta não seja encontrada.");

        // Garante de forma defensiva que a verificação de senha não foi sequer tentada
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).gerarToken(any(Conta.class));
    }
}