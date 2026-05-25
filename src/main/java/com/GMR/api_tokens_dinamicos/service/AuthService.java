package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.dto.LoginRequestDTO;
import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Credencial;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final ContaRepository contaRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(ContaRepository contaRepository, PasswordEncoder passwordEncoder) {
        this.contaRepository = contaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean validarLogin(LoginRequestDTO loginDTO) {
        // Busca a conta pela agência e número
        Optional<Conta> contaOpt = contaRepository.findByAgenciaAndNumeroConta(loginDTO.agencia(), loginDTO.numeroConta());

        if (contaOpt.isEmpty()) {
            return false;
        }

        Conta conta = contaOpt.get();
        Credencial credencial = conta.getCredencial();

        if (credencial == null) {
            return false;
        }

        // Verifica se a senha digitada bate com o Hash criptografado no banco
        return passwordEncoder.matches(loginDTO.senha(), credencial.getHashSenha());
    }
}