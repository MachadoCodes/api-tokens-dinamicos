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

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("🚨 --- INICIANDO VERIFICAÇÃO DO JWT NO FILTRO ---");
        final String authHeader = request.getHeader("Authorization");
        System.out.println("👉 Cabeçalho que chegou: " + authHeader);

        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extrairUsername(jwt);
                System.out.println("✅ Username extraído do Token: " + username);
            } catch (Exception e) {
                System.out.println("❌ ERRO: Token JWT inválido, adulterado ou expirado. Detalhe: " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Nenhum cabeçalho 'Bearer' encontrado nesta requisição.");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            boolean isValido = jwtUtil.isTokenValido(jwt, username);
            System.out.println("🔎 Verificação matemática do token é válida? " + isValido);

            if (isValido){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("🔓 CATRACA LIBERADA para a conta: " + username);
            } else {
                System.out.println("🔒 CATRACA BLOQUEADA. A matemática do token falhou.");
            }
        }

        System.out.println("🏁 --- FIM DA VERIFICAÇÃO ---");
        filterChain.doFilter(request, response);
    }
}