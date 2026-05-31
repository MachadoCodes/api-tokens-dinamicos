package com.GMR.api_tokens_dinamicos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRequestDTO(

        @NotBlank(message = "O nome de usuário (username) é obrigatório.")
        @Size(max = 255, message = "O nome de usuário deve conter no máximo 255 caracteres.")
        String username,

        @NotBlank(message = "A senha (password) é obrigatória.")
        @Pattern(regexp = "^\\d{4}$", message = "A senha de acesso eletrônico deve conter exatamente 4 números.")
        String password

) {
}