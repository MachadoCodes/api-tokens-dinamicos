package com.GMR.api_tokens_dinamicos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContaRequestDTO(
        @NotBlank(message = "O número da conta é obrigatório.")
        @Size(max = 40, message = "O número da conta deve conter no máximo 40 caracteres.")
        String numeroConta,

        @NotBlank(message = "O número da agência é obrigatório.")
        @Size(max = 10, message = "A agência deve conter no máximo 10 caracteres.")
        String agencia,

        @NotBlank(message = "A senha é obrigatória.")
        @Pattern(regexp = "^\\d{4}$", message = "A senha de acesso eletrônico deve conter exatamente 4 números.")
        String senha
) {
}