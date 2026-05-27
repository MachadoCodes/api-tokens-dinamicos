package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.dto.LoginRequestDTO;
import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Credencial;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import com.GMR.api_tokens_dinamicos.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // Nova injeção do gerador de tokens

    public AuthService(ContaRepository contaRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.contaRepository = contaRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Valida as credenciais. Se estiverem corretas, orquestra a geração do JWT com as claims.
     * Retorna null se a senha estiver errada ou a conta não existir.
     */
    public String autenticarEGerarToken(LoginRequestDTO loginDTO) {
        Optional<Conta> contaOpt = contaRepository.findByAgenciaAndNumeroConta(loginDTO.agencia(), loginDTO.numeroConta());

        if (contaOpt.isPresent()) {
            Conta conta = contaOpt.get();
            Credencial credencial = conta.getCredencial();

            // Verifica se a credencial existe e se o hash da senha bate
            if (credencial != null && passwordEncoder.matches(loginDTO.senha(), credencial.getHashSenha())) {

                // Login válido! A mágica acontece aqui: geramos o JWT enriquecido com nome e id.
                return jwtUtil.gerarToken(conta);
            }
        }

        return null; // Retorna nulo em caso de falha de autenticação (Unauthorized)
    }
}