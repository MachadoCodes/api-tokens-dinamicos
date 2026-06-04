package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.dto.UsuarioRequestDTO;
import com.GMR.api_tokens_dinamicos.model.Usuario;
import com.GMR.api_tokens_dinamicos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioMock;
    private UsuarioRequestDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNomeUsuario("Renato Machado");
        usuarioMock.setCpf("12345678909");
        usuarioMock.setEmail("teste@teste.com");
        usuarioMock.setAtivo(true);

        usuarioDTO = new UsuarioRequestDTO("Renato Editado", "12345678909", "teste@teste.com", "11111111", "999999999");
    }

    @Test
    @DisplayName("Deve retornar todos os usuários ativos")
    void testFindAll() {
        when(usuarioRepository.findByAtivoTrue()).thenReturn(List.of(usuarioMock));
        List<Usuario> result = usuarioService.findAll();
        assertFalse(result.isEmpty());
        verify(usuarioRepository, times(1)).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID apenas se estiver ativo")
    void testFindUsuarioById() {
        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuarioMock));
        Optional<Usuario> result = usuarioService.findUsuarioById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Deve buscar usuário por CPF")
    void testFindByCpf() {
        when(usuarioRepository.findByCpf("12345678909")).thenReturn(Optional.of(usuarioMock));
        Optional<Usuario> result = usuarioService.findByCpf("12345678909");
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Deve buscar usuário por Email")
    void testFindByEmail() {
        when(usuarioRepository.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuarioMock));
        Optional<Usuario> result = usuarioService.findByEmail("teste@teste.com");
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Deve salvar um novo usuário garantindo que inicie ativo")
    void testSaveUsuario() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);
        Usuario result = usuarioService.saveUsuario(usuarioDTO);

        assertNotNull(result);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve desativar (exclusão lógica) usuário com sucesso")
    void testDisableUsuarioById_Sucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        usuarioService.disableUsuarioById(1L);

        assertFalse(usuarioMock.isAtivo());
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar desativar usuário inexistente")
    void testDisableUsuarioById_NaoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> usuarioService.disableUsuarioById(99L));
    }

    @Test
    @DisplayName("Deve reativar usuário com sucesso")
    void testEnableUsuarioById_Sucesso() {
        usuarioMock.setAtivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        usuarioService.enableUsuarioById(1L);

        assertTrue(usuarioMock.isAtivo());
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar reativar usuário inexistente")
    void testEnableUsuarioById_NaoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> usuarioService.enableUsuarioById(99L));
    }

    @Test
    @DisplayName("Deve atualizar os dados do usuário com sucesso")
    void testUpdateUsuarioById_Sucesso() {
        when(usuarioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);

        Usuario result = usuarioService.updateUsuarioById(1L, usuarioDTO);

        assertNotNull(result);
        assertEquals("Renato Editado", usuarioMock.getNomeUsuario());
        verify(usuarioRepository, times(1)).save(usuarioMock);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar usuário inexistente")
    void testUpdateUsuarioById_NaoEncontrado() {
        when(usuarioRepository.findByIdAndAtivoTrue(99L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> usuarioService.updateUsuarioById(99L, usuarioDTO));
    }
}