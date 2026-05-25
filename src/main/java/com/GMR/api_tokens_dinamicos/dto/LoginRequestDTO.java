package com.GMR.api_tokens_dinamicos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "A agência é obrigatória.")
        @Size(max = 10, message = "A agência deve conter no máximo 10 caracteres.")
        String agencia,

        @NotBlank(message = "O número da conta é obrigatório.")
        @Size(max = 40, message = "O número da conta deve conter no máximo 40 caracteres.")
        String numeroConta,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, max = 100, message = "A senha deve conter entre 6 e 100 caracteres.")
        String senha
) {
}