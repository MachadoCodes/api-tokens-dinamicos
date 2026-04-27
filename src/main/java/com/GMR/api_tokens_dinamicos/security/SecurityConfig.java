package com.GMR.api_tokens_dinamicos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe de configuração central do Spring Security para o projeto.
 * Define as regras de acesso, políticas de sessão e a integração do filtro JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Define a corrente de filtros de segurança (Security Filter Chain).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Desabilita CSRF, pois em APIs Stateless com JWT não é necessário.
                .csrf(csrf -> csrf.disable())

                // 2. Configura as regras de autorização das rotas
                .authorizeHttpRequests(auth -> auth

                        // ABRINDO A PORTA DA RECEPÇÃO: Qualquer um pode tentar fazer login
                        .requestMatchers("/api/v1/auth/login").permitAll()

                        // LIBERANDO A CRIAÇÃO DE USUÁRIOS (Regra que estava no outro arquivo que o  Miguel  criou)
                        .requestMatchers("/usuarios/**").permitAll()

                        // No momento, estamos trancando o resto dentro de /api/v1/**
                        // Para acessar, o cliente precisará do Token JWT no cabeçalho.
                        .requestMatchers("/api/v1/tokens/**").authenticated()

                        // Caso queira deixar alguma rota pública (ex: login), usaria .permitAll()
                        .anyRequest().authenticated()
                )

                // 3. Define que a API não salvará estado de sessão (Stateless)
                // Isso obriga que cada requisição traga seu próprio Token JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Adiciona o nosso segurança personalizado (JwtFilter) na fila
                // Ele deve rodar ANTES do filtro padrão de autenticação do Spring
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * Gerenciador de Autenticação (Necessário para o AuthController validar a senha)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Cria um usuário "oficial" do sistema de comunicação do banco.
     * Em um cenário pós-acadêmico, isso seria substituído por uma busca no banco de dados.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails appDoBanco = User.builder()
                .username("banco-app-oficial")
                .password("{noop}senhaSegura123") // {noop} indica que a senha não é codificada (apenas para testes)
                .roles("SISTEMA")
                .build();
        return new InMemoryUserDetailsManager(appDoBanco);
    }
}
