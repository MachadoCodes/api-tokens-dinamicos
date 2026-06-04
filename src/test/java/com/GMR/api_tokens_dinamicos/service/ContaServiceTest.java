package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.dto.ContaRequestDTO;
import com.GMR.api_tokens_dinamicos.model.Comunicacao;
import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Credencial;
import com.GMR.api_tokens_dinamicos.model.Usuario;
import com.GMR.api_tokens_dinamicos.repository.ComunicacaoRepository;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import com.GMR.api_tokens_dinamicos.repository.CredencialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ComunicacaoRepository comunicacaoRepository;

    @Mock
    private CredencialRepository credencialRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ContaService contaService;

    private Conta contaMock;
    private Usuario usuarioMock;
    private ContaRequestDTO contaDTO;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);

        contaMock = new Conta();
        contaMock.setId(10L);
        contaMock.setNumeroConta("12345-6");
        contaMock.setAgencia("1234");
        contaMock.setUsuario(usuarioMock);
        contaMock.setAtivo(true);

        contaDTO = new ContaRequestDTO("1234", "12345-6", "1234");
    }

    @Test
    @DisplayName("Deve retornar todas as contas vinculadas a um usuário")
    void testFindAllById() {
        when(contaRepository.findByUsuarioIdAndAtivoTrue(1L)).thenReturn(List.of(contaMock));
        List<Conta> result = contaService.findAllById(1L);
        assertFalse(result.isEmpty());
        verify(contaRepository, times(1)).findByUsuarioIdAndAtivoTrue(1L);
    }

    @Test
    @DisplayName("Deve buscar conta por ID")
    void testFindContaById() {
        when(contaRepository.findByIdAndAtivoTrue(10L)).thenReturn(Optional.of(contaMock));
        Optional<Conta> result = contaService.findContaById(10L);
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Deve buscar conta por Agência e Número")
    void testFindContaByAgenciaAndNumero() {
        when(contaRepository.findByAgenciaAndNumeroConta("1234", "12345-6")).thenReturn(Optional.of(contaMock));
        Optional<Conta> result = contaService.findContaByAgenciaAndNumero("1234", "12345-6");
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Deve salvar nova conta e criar credencial blindada com BCrypt")
    void testSaveConta_Sucesso() {
        // Ensinando o comportamento do caminho feliz
        when(usuarioService.findUsuarioById(1L)).thenReturn(Optional.of(usuarioMock));
        when(contaRepository.save(any(Conta.class))).thenReturn(contaMock);

        // Simula o encriptador de senha
        when(passwordEncoder.encode("1234")).thenReturn("$2a$10$HashSimuladoSuperSeguro");
        when(credencialRepository.save(any(Credencial.class))).thenReturn(new Credencial());

        Conta result = contaService.saveConta(1L, contaDTO);

        assertNotNull(result);
        // Garante que o encoder foi chamado para proteger a senha antes de salvar
        verify(passwordEncoder, times(1)).encode("1234");
        verify(credencialRepository, times(1)).save(any(Credencial.class));
    }

    @Test
    @DisplayName("Deve estourar erro ao tentar criar conta para usuário inexistente")
    void testSaveConta_UsuarioNaoEncontrado() {
        when(usuarioService.findUsuarioById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            contaService.saveConta(99L, contaDTO);
        });

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
        // Garante que se o usuário não existe, a conta NÃO foi salva
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    @DisplayName("Deve desativar conta com sucesso")
    void testDisableContaById_Sucesso() {
        when(contaRepository.findById(10L)).thenReturn(Optional.of(contaMock));
        contaService.disableContaById(10L);
        assertFalse(contaMock.isAtivo());
        verify(contaRepository, times(1)).save(contaMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar desativar conta inexistente")
    void testDisableContaById_NaoEncontrado() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> contaService.disableContaById(99L));
    }

    @Test
    @DisplayName("Deve reativar conta com sucesso")
    void testEnableContaById_Sucesso() {
        contaMock.setAtivo(false);
        when(contaRepository.findById(10L)).thenReturn(Optional.of(contaMock));
        contaService.enableContaById(10L);
        assertTrue(contaMock.isAtivo());
        verify(contaRepository, times(1)).save(contaMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reativar conta inexistente")
    void testEnableContaById_NaoEncontrado() {
        when(contaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> contaService.enableContaById(99L));
    }

    @Test
    @DisplayName("Deve buscar histórico de comunicações da conta")
    void testBuscarHistoricoPorConta() {
        when(comunicacaoRepository.findByContaId(10L)).thenReturn(List.of(new Comunicacao()));
        List<Comunicacao> result = contaService.buscarHistoricoPorConta(10L);
        assertFalse(result.isEmpty());
        verify(comunicacaoRepository, times(1)).findByContaId(10L);
    }
}