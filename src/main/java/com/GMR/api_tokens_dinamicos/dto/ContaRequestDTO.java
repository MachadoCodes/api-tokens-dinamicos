package com.GMR.api_tokens_dinamicos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContaRequestDTO(
        @NotBlank(message = "O número da conta é obrigatório.")
        @Size(max = 40, message = "O número da conta deve conter no máximo 40 caracteres.")
        String numeroConta,

        @NotBlank(message = "O número da agência é obrigatório.")
        @Size(max = 10, message = "A agência deve conter no máximo 10 caracteres.")
        String agencia,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, max = 100, message = "A senha deve conter entre 8 e 100 caracteres.")
        String senha
) {
}