package com.GMR.api_tokens_dinamicos.controller;

import com.GMR.api_tokens_dinamicos.dto.ContaRequestDTO;
import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Comunicacao;
import com.GMR.api_tokens_dinamicos.security.JwtUtil;
import com.GMR.api_tokens_dinamicos.service.ContaService;
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

@WebMvcTest(ContaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ContaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContaService contaService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private Conta contaMock;

    @BeforeEach
    void setUp() {
        contaMock = new Conta();
        contaMock.setId(10L);
        contaMock.setNumeroConta("12345-6");
        contaMock.setAgencia("1234");
    }

    @Test
    @DisplayName("Deve retornar 200 ao buscar conta por ID existente")
    void testGetContaById_Sucesso() throws Exception {
        when(contaService.findContaById(10L)).thenReturn(Optional.of(contaMock));

        mockMvc.perform(get("/usuarios/1/contas/10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar conta por ID inexistente")
    void testGetContaById_NaoEncontrado() throws Exception {
        when(contaService.findContaById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/1/contas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar lista de contas atreladas ao usuário")
    void testGetAllContasByUsuarioId() throws Exception {
        when(contaService.findAllById(1L)).thenReturn(List.of(contaMock));

        mockMvc.perform(get("/usuarios/1/contas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroConta").value("12345-6"));
    }

    @Test
    @DisplayName("Deve retornar 200 ao buscar por agência e número de conta existentes")
    void testFindContaByAgenciaAndNumero_Sucesso() throws Exception {
        when(contaService.findContaByAgenciaAndNumero("1234", "12345-6")).thenReturn(Optional.of(contaMock));

        mockMvc.perform(get("/usuarios/1/contas/busca?agencia=1234&numeroConta=12345-6"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar por agência e número de conta inexistentes")
    void testFindContaByAgenciaAndNumero_NaoEncontrado() throws Exception {
        when(contaService.findContaByAgenciaAndNumero("0000", "00000-0")).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/1/contas/busca?agencia=0000&numeroConta=00000-0"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve criar uma nova conta e retornar 201 Created")
    void testCreateConta() throws Exception {
        when(contaService.saveConta(eq(1L), any(ContaRequestDTO.class))).thenReturn(contaMock);

        // CORREÇÃO: "senha" agora tem exatamente 4 dígitos numéricos
        mockMvc.perform(post("/usuarios/1/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"agencia\": \"1234\", \"numeroConta\": \"12345-6\", \"senha\": \"1234\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Deve deletar (desativar) uma conta e retornar 204 No Content")
    void testDeleteContaById() throws Exception {
        doNothing().when(contaService).disableContaById(10L);

        mockMvc.perform(delete("/usuarios/1/contas/10"))
                .andExpect(status().isNoContent());

        verify(contaService, times(1)).disableContaById(10L);
    }

    @Test
    @DisplayName("Deve reativar uma conta e retornar 204 No Content")
    void testReactiveContaById() throws Exception {
        doNothing().when(contaService).enableContaById(10L);

        mockMvc.perform(patch("/usuarios/1/contas/10/reativar"))
                .andExpect(status().isNoContent());

        verify(contaService, times(1)).enableContaById(10L);
    }

    @Test
    @DisplayName("Deve retornar 200 com o histórico de tokens da conta")
    void testBuscarHistoricoDeTokens() throws Exception {
        Comunicacao hist = new Comunicacao();
        when(contaService.buscarHistoricoPorConta(10L)).thenReturn(List.of(hist));

        mockMvc.perform(get("/usuarios/1/contas/10/historico-tokens"))
                .andExpect(status().isOk());
    }
}