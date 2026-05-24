package com.GMR.api_tokens_dinamicos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record UsuarioRequestDTO(

        @NotBlank(message = "O nome de usuário é obrigatório.")
        @Size(max = 150, message = "O nome deve conter no máximo 150 caracteres.")
        String nomeUsuario,

        @NotBlank(message = "O CPF é obrigatório.")
        @CPF(message = "O CPF informado é inválido.") // Valida a regra matemática real de CPFs brasileiros!
        String cpf,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O formato do e-mail é inválido.")
        @Size(max = 255, message = "O e-mail deve conter no máximo 255 caracteres.")
        String email,

        // Telefone fixo geralmente não tem @NotBlank pois pode ser opcional, mas limitamos o tamanho para segurança
        @Size(max = 20, message = "O telefone fixo deve conter no máximo 20 caracteres.")
        String telefoneFixo,

        @NotBlank(message = "O telefone celular é obrigatório.")
        @Size(max = 20, message = "O telefone celular deve conter no máximo 20 caracteres.")
        String telefoneCelular

) {
}