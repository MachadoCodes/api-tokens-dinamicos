package com.GMR.api_tokens_dinamicos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * O Filtro de Segurança. Ele intercepta TODAS as requisições HTTP antes que elas cheguem aos Controllers.
 * Herda de OncePerRequestFilter para garantir que o segurança verifique o crachá apenas uma vez por requisição.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    // Injeta a nossa "máquina de ler crachás"
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Pega o cabeçalho de autorização da requisição (onde o token deve estar escondido)
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 2. Verifica se a pessoa mandou o cabeçalho e se ele começa com o padrão "Bearer " (Portador)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Recorta a palavra "Bearer " e guarda só a string do token
            try {
                username = jwtUtil.extrairUsername(jwt); // Tenta ler de quem é o token
            } catch (Exception e) { // Se o token for falso, tiver a assinatura errada ou estiver expirado, a biblioteca gera um erro e cai aqui.
                System.out.println("Atenção: Token JWT inválido ou expirado.");
            }
        }

        // 3. Se achamos um usuário válido no token e ele ainda não passou pela catraca do Spring Security...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. Pede para o JwtUtil fazer a verificação matemática rigorosa de que o token é legítimo
            // (assinatura correta, não adulterado, não expirado)
            if (jwtUtil.isTokenValido(jwt, username)){

                // 5. O token é legítimo! Criamos um passe de acesso oficial do Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,
                        null, new ArrayList<>());

                // 6. Coloca esse passe de acesso no contexto de segurança (Isso abre a catraca virtual)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Libera o fluxo. Se a catraca foi aberta no passo 6, ele acessa o Controller.
        // Se não foi, o Spring Security vai barrar o acesso logo em seguida.
        filterChain.doFilter(request, response);
    }
}
