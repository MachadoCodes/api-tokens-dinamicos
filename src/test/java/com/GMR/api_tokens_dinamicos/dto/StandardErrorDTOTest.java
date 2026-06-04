package com.GMR.api_tokens_dinamicos.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StandardErrorDTOTest {

    @Test
    @DisplayName("Deve instanciar o StandardErrorDTO corretamente")
    void testStandardErrorDTOCreation() {
        LocalDateTime agora = LocalDateTime.now();
        List<String> mensagens = List.of("Erro 1", "Erro 2");

        StandardErrorDTO dto = new StandardErrorDTO(agora, 400, "Bad Request", mensagens);

        assertNotNull(dto);
        assertEquals(agora, dto.timestamp());
        assertEquals(400, dto.status());
        assertEquals("Bad Request", dto.erro());
        assertEquals(2, dto.mensagens().size());
    }
}