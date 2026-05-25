package com.GMR.api_tokens_dinamicos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequestDTO(

        @NotBlank(message = "O nome de usuário (username) é obrigatório.")
        @Size(max = 255, message = "O nome de usuário deve conter no máximo 255 caracteres.")
        String username,

        @NotBlank(message = "A senha (password) é obrigatória.")
        @Size(min = 6, max = 100, message = "A senha deve conter entre 6 e 100 caracteres.")
        String password

) {
}