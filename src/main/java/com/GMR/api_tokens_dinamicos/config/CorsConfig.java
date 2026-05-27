package com.GMR.api_tokens_dinamicos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracao = new CorsConfiguration();

        // 1. Quem pode acessar? (As URLs do front-end)
        // Colocamos as portas mais comuns de desenvolvimento. Ajuste conforme necessário.
        configuracao.setAllowedOrigins(Arrays.asList(
                "http://localhost:63343",   // Servidor do IntelliJ
                "http://localhost:5500",    // Porta padrão do Live Server (VS Code)
                "http://127.0.0.1:5500",
                "http://localhost:3000",    // Porta padrão do React/Next.js
                "http://localhost:5173"     // Porta padrão do Vite
        ));

        // 2. Quais métodos HTTP o Miguel pode usar?
        configuracao.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 3. Quais cabeçalhos o navegador pode enviar? (Crucial para o JWT passar!)
        configuracao.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // 4. Permite envio de credenciais (cookies/tokens de autorização)
        configuracao.setAllowCredentials(true);

        // Aplica essas regras para todas as rotas da nossa API (/**)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuracao);

        return source;
    }
}