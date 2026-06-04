package com.GMR.api_tokens_dinamicos.security;

import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Usuario;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private Conta contaMock;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Simula o @Value injetando uma chave falsa (precisa ter mais de 32 caracteres para o HMAC-SHA)
        ReflectionTestUtils.setField(jwtUtil, "chaveSecretaString", "MinhaChaveSuperSecretaParaTestesA3ComMaisDe32Caracteres!");
        jwtUtil.init(); // Chama o @PostConstruct manualmente para criar a SecretKey

        // Prepara os dados da Conta/Usuário que o JwtUtil precisa para montar o payload
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario("Nome De Teste");

        contaMock = new Conta();
        contaMock.setId(10L);
        contaMock.setNumeroConta("12345-6");
        contaMock.setUsuario(usuario);
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido e extrair as claims corretamente")
    void testGerarEExtrairToken() {
        // Act: Gera o token
        String token = jwtUtil.gerarToken(contaMock);

        // Assert: Verifica se o token foi gerado
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Act: Abre o "cofre" do token para ver o que tem dentro
        Claims dados = jwtUtil.extrairDados(token);

        // Assert: Garante que os dados (id, nome e numeroConta) foram gravados corretamente
        assertEquals("12345-6", dados.getSubject());
        assertEquals(10, dados.get("id", Integer.class));
        assertEquals("Nome De Teste", dados.get("nome", String.class));
    }

    @Test
    @DisplayName("Deve validar a autenticidade do token para o usuário correto")
    void testIsTokenValido_Sucesso() {
        String token = jwtUtil.gerarToken(contaMock);

        boolean valido = jwtUtil.isTokenValido(token, "12345-6");

        assertTrue(valido, "O token deve ser aceito como válido.");
    }

    @Test
    @DisplayName("Deve invalidar o token se o usuário fornecido for diferente")
    void testIsTokenValido_FalhaUsuarioDiferente() {
        String token = jwtUtil.gerarToken(contaMock);

        boolean valido = jwtUtil.isTokenValido(token, "00000-0");

        assertFalse(valido, "O token não pode ser aceito para outra conta.");
    }
}