package com.GMR.api_tokens_dinamicos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // <-- Nova importação
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import jakarta.annotation.PostConstruct; // <-- Nova importação

/**
 * Classe utilitária responsável pela geração, assinatura e validação dos tokens JWT.
 * Utiliza a biblioteca JJWT atualizada para garantir a integridade das requisições.
 * O componente '@Component' permite que o Spring injete esta classe onde for necessário.
 */
@Component
public class JwtUtil {

    // O Spring injeta o valor configurado no application.properties direto aqui
    @Value("${jwt.secret}")
    private String chaveSecretaString;

    // Removemos o 'final' e a inicialização direta para fazer isso no método init()
    private SecretKey CHAVE_SECRETA;

    // Tempo de validade do Token JWT de acesso em milissegundos (15 minutos)
    private final long TEMPO_EXPIRACAO_MS = 900000;

    /**
     * Método de inicialização que roda automaticamente LOGO APÓS o Spring
     * injetar o valor da variável de ambiente na String 'chaveSecretaString'.
     */
    @PostConstruct
    public void init() {
        this.CHAVE_SECRETA = Keys.hmacShaKeyFor(chaveSecretaString.getBytes());
    }

    /**
     * Gera um novo Token JWT para um usuário específico.
     */
    public String gerarToken(String username) {
        return Jwts.builder()
                .subject(username) // Define a quem pertence o token
                .issuedAt(new Date()) // Data e hora de criação
                .expiration(new Date(System.currentTimeMillis() + TEMPO_EXPIRACAO_MS)) // Data de expiração
                .signWith(CHAVE_SECRETA) // Assinatura digital que impede adulterações
                .compact(); // Constrói a string final do JWT
    }

    /**
     * Extrai os dados (Claims) de dentro de um token recebido.
     */
    public Claims extrairDados(String token) {
        return Jwts.parser()
                .verifyWith(CHAVE_SECRETA) // Confirma se o token foi assinado pela nossa API
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Descobre de quem é o token lendo o "subject".
     */
    public String extrairUsername(String token) {
        return extrairDados(token).getSubject();
    }

    /**
     * Valida se o token ainda está dentro do tempo de vida e se pertence ao usuário.
     */
    public boolean isTokenValido(String token, String username) {
        final String usernameDoToken = extrairUsername(token);
        final Date dataExpiracao = extrairDados(token).getExpiration();

        // Retorna true se o nome bater E a data atual for antes da expiração
        return (usernameDoToken.equals(username) && !dataExpiracao.before(new Date()));
    }
}