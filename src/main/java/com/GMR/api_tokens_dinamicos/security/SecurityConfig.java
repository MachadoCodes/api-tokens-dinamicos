package com.GMR.api_tokens_dinamicos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                // 1. Ativa o CORS para o Frontend não ser bloqueado
                .cors(Customizer.withDefaults()) 

                // 2. Desabilita CSRF, pois em APIs Stateless com JWT não é necessário.
                .csrf(csrf -> csrf.disable())

                // 3. Configura as regras de autorização das rotas
                .authorizeHttpRequests(auth -> auth

                        // ABRINDO A PORTA DA RECEPÇÃO: Qualquer um pode tentar fazer login
                        .requestMatchers("/api/v1/auth/login").permitAll()

                        // LIBERANDO APENAS A CRIAÇÃO DE USUÁRIOS (POST)
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                        // LIBERANDO APENAS A CRIAÇÃO DE CONTAS (POST)
                        .requestMatchers(HttpMethod.POST, "/usuarios/*/contas").permitAll()

                        // FECHA A PORTA: Todo o resto (histórico, geração de token, etc.) exige JWT
                        .anyRequest().authenticated()
                )

                // 4. Define que a API não salvará estado de sessão (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 5. Adiciona o nosso segurança personalizado (JwtFilter) na fila
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
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails appDoBanco = User.builder()
                .username("banco-app-oficial")
                .password("{noop}senhaSegura123")
                .roles("SISTEMA")
                .build();
        return new InMemoryUserDetailsManager(appDoBanco);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
