package com.GMR.api_tokens_dinamicos.controller;
import com.GMR.api_tokens_dinamicos.dto.AuthRequestDTO;
import com.GMR.api_tokens_dinamicos.security.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequestDTO request){
        try{
            // 1. Tenta autenticar o usuário com as credenciais fornecidas
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            // 2. Se a senha estiver correta, gera o Token JWT
            String tokenJwt = jwtUtil.gerarToken(authentication.getName());

            // 3. Devolve o token para o cliente
            return ResponseEntity.ok(tokenJwt);

        } catch (Exception e){
            // Se a senha estiver errada, retorna 401 Unauthorized
            return ResponseEntity.status(401).body("Credenciais inválidas. Acesso negado.");
        }
    }
}
