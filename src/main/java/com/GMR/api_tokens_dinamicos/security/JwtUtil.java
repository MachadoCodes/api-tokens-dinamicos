package com.GMR.api_tokens_dinamicos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Classe utilitária responsável pela geração, assinatura e validação dos tokens JWT.
 * Utiliza a biblioteca JJWT atualizada para garantir a integridade das requisições.
 * O componente '@Component' permite que o Spring injete esta classe onde for necessário.
 */
@Component
public class JwtUtil {

    // Chave secreta usada para assinar o token.
    // Em produção, isso NUNCA deve ficar no código, mas sim em variáveis de ambiente.
    // Esta chave precisa ter pelo menos 256 bits (32 caracteres) para o algoritmo HS256.
    // Dica para cenário real: Em um ambiente real de produção, esta chave ficaria escondida nas variáveis de ambiente.
    private final String CHAVE_SECRETA_STRING = "TrustTokenAcessoSeguroBancario2026@GMR!";

    // Converte a string acima para um formato de chave criptográfica real
    private final SecretKey CHAVE_SECRETA = Keys.hmacShaKeyFor(CHAVE_SECRETA_STRING.getBytes());

    // Tempo de validade do Token JWT de acessoem milessegundos (Ex: 1/4 de hora)
    private final long TEMPO_EXPIRACAO_MS = 900000;

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