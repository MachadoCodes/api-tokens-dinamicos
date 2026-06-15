package com.GMR.api_tokens_dinamicos.controller;

import com.GMR.api_tokens_dinamicos.dto.UsuarioRequestDTO;
import com.GMR.api_tokens_dinamicos.model.Usuario;
import com.GMR.api_tokens_dinamicos.security.JwtUtil;
import com.GMR.api_tokens_dinamicos.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false) // Ignora o porteiro de segurança para focar na rota
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNomeUsuario("Usuario Teste");
    }

    @Test
    @DisplayName("Deve listar todos os usuários (GET /usuarios)")
    void testGetAllUsuarios() throws Exception {
        when(usuarioService.findAll()).thenReturn(List.of(usuarioMock));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeUsuario").value("Usuario Teste"));
    }

    @Test
    @DisplayName("Deve retornar 200 ao buscar usuário por ID existente")
    void testGetUsuarioById_Sucesso() throws Exception {
        when(usuarioService.findUsuarioById(1L)).thenReturn(Optional.of(usuarioMock));

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar usuário por ID inexistente")
    void testGetUsuarioById_NaoEncontrado() throws Exception {
        when(usuarioService.findUsuarioById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 200 ao buscar por CPF existente")
    void testFindByCpf_Sucesso() throws Exception {
        when(usuarioService.findByCpf("12345678909")).thenReturn(Optional.of(usuarioMock));

        mockMvc.perform(get("/usuarios/cpf/12345678909"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar por CPF inexistente")
    void testFindByCpf_NaoEncontrado() throws Exception {
        when(usuarioService.findByCpf("000")).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/cpf/000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 200 ao buscar por Email existente")
    void testFindByEmail_Sucesso() throws Exception {
        when(usuarioService.findByEmail("teste@teste.com")).thenReturn(Optional.of(usuarioMock));

        mockMvc.perform(get("/usuarios/email/teste@teste.com"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar por Email inexistente")
    void testFindByEmail_NaoEncontrado() throws Exception {
        when(usuarioService.findByEmail("fantasma@teste.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/email/fantasma@teste.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve criar um novo usuário e retornar 201 Created (POST /usuarios)")
    void testCreateUsuario() throws Exception {
        when(usuarioService.saveUsuario(any(UsuarioRequestDTO.class))).thenReturn(usuarioMock);

        // JSON formatado exatamente como o UsuarioRequestDTO e com CPF válido
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeUsuario\": \"Usuario Teste\", \"cpf\": \"12345678909\", \"email\": \"teste@teste.com\", \"telefoneCelular\": \"11999999999\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve atualizar um usuário e retornar 200 OK (PUT /usuarios/{id})")
    void testUpdateUsuarioById() throws Exception {
        when(usuarioService.updateUsuarioById(eq(1L), any(UsuarioRequestDTO.class))).thenReturn(usuarioMock);

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeUsuario\": \"Usuario Editado\", \"cpf\": \"12345678909\", \"email\": \"teste@teste.com\", \"telefoneCelular\": \"11999999999\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve deletar (desativar) um usuário e retornar 204 No Content")
    void testDeleteUsuarioById() throws Exception {
        doNothing().when(usuarioService).disableUsuarioById(1L);

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).disableUsuarioById(1L);
    }

    @Test
    @DisplayName("Deve reativar um usuário e retornar 204 No Content")
    void testReactiveUsuarioById() throws Exception {
        doNothing().when(usuarioService).enableUsuarioById(1L);

        mockMvc.perform(patch("/usuarios/1/reativar"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).enableUsuarioById(1L);
    }
}