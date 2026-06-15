package com.GMR.api_tokens_dinamicos.dto;

import java.time.LocalDateTime;

public record TokenHistoricoDTO(
        String codigo,
        String canal,
        LocalDateTime dataGeracao,
        String status
) {}