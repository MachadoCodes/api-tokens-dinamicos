package com.GMR.api_tokens_dinamicos.controller;

import com.GMR.api_tokens_dinamicos.dto.LoginRequestDTO;
import com.GMR.api_tokens_dinamicos.service.AuthService;
import com.GMR.api_tokens_dinamicos.security.JwtUtil;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // Injetamos os DOIS serviços: o que valida a senha e o que gera o token
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO loginDTO) {

        // 1. Usa o nosso serviço para validar Agência + Conta + Senha com o BCrypt no banco
        boolean credenciaisValidas = authService.validarLogin(loginDTO);

        if (credenciaisValidas) {
            // 2. Se a senha estiver correta, gera o Token JWT
            // Vamos usar o número da conta como a "identidade" principal dentro do token
            String tokenJwt = jwtUtil.gerarToken(loginDTO.numeroConta());

            // 3. Devolve o token real e criptografado para o cliente
            return ResponseEntity.ok(tokenJwt);

        } else {
            // Se a senha, agência ou conta estiverem erradas, retorna 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Agência, conta ou senha incorretos. Acesso negado.");
        }
    }
}