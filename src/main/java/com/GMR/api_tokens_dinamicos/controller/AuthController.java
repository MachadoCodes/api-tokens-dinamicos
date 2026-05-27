package com.GMR.api_tokens_dinamicos.controller;

import com.GMR.api_tokens_dinamicos.dto.LoginRequestDTO;
import com.GMR.api_tokens_dinamicos.service.AuthService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // Injetamos apenas o AuthService, pois ele agora orquestra a validação e a geração do token
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO loginDTO) {

        // 1. O AuthService valida as credenciais e, se corretas, já devolve o JWT enriquecido com nome e ID
        String tokenJwt = authService.autenticarEGerarToken(loginDTO);

        if (tokenJwt != null) {
            // 2. Sucesso! Devolve o token real para o front-end armazenar
            return ResponseEntity.ok(tokenJwt);
        } else {
            // 3. Se a senha, agência ou conta estiverem erradas, retorna nulo e cai aqui no 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Agência, conta ou senha incorretos. Acesso negado.");
        }
    }
}