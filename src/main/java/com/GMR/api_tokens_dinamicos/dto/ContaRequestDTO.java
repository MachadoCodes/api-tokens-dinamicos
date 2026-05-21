package com.GMR.api_tokens_dinamicos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContaRequestDTO(
        @NotBlank(message = "O número da conta é obrigatório")
        String numeroConta,

        @NotBlank(message = "O número da agência é obrigatório")
        String agencia,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String senha
) {
}
