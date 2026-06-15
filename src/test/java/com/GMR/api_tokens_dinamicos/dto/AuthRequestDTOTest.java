package com.GMR.api_tokens_dinamicos.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthRequestDTOTest {

    @Test
    @DisplayName("Deve instanciar o AuthRequestDTO corretamente")
    void testAuthRequestDTOCreation() {
        AuthRequestDTO dto = new AuthRequestDTO("12345-6", "1234");

        assertNotNull(dto);
        assertEquals("12345-6", dto.username());
        assertEquals("1234", dto.password());
    }
}