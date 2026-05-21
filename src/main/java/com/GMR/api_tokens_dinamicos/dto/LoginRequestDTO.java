package com.GMR.api_tokens_dinamicos.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "A agência é obrigatória")
        String agencia,

        @NotBlank(message = "O número da conta é obrigatório")
        String numeroConta,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {
}
