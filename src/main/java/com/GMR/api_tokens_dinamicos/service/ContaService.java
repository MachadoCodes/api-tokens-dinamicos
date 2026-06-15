package com.GMR.api_tokens_dinamicos.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder; // Novo import
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.GMR.api_tokens_dinamicos.dto.ContaRequestDTO; // Novo import
import com.GMR.api_tokens_dinamicos.model.Credencial;   // Novo import
import com.GMR.api_tokens_dinamicos.model.Comunicacao;
import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.repository.ComunicacaoRepository;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import com.GMR.api_tokens_dinamicos.repository.CredencialRepository; // Novo import

@Service
public class ContaService {
    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ComunicacaoRepository comunicacaoRepository;

    // 👇 Novas injeções para a blindagem de segurança
    @Autowired
    private CredencialRepository credencialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Conta> findAllById(Long usuarioId) {
        return contaRepository.findByUsuarioIdAndAtivoTrue(usuarioId);
    }

    public Optional<Conta> findContaById(Long contaId) {
        return contaRepository.findByIdAndAtivoTrue(contaId);
    }

    public Optional<Conta> findContaByAgenciaAndNumero(String agencia, String numeroDaConta) {
        return contaRepository.findByAgenciaAndNumeroConta(agencia, numeroDaConta);
    }

    /**
     * Fluxo seguro de criação de conta e credencial criptografada (BCrypt)
     */
    public Conta saveConta(Long usuarioId, ContaRequestDTO dto) {
        return usuarioService.findUsuarioById(usuarioId).map(usuarioEncontrado -> {

            // 1. Instancia a nova conta com os dados do DTO
            Conta contaNova = new Conta(dto.numeroConta(), dto.agencia(), usuarioEncontrado);
            Conta contaSalva = contaRepository.save(contaNova);

            // 2. Transforma a senha pura em um Hash seguro usando o BCrypt
            String senhaCriptografada = passwordEncoder.encode(dto.senha());

            // 3. Cria a credencial vinculada à conta e com a senha blindada
            Credencial credencial = new Credencial(contaSalva, senhaCriptografada);
            credencialRepository.save(credencial);

            return contaSalva;

        }).orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + usuarioId));
    }

    public void disableContaById(Long contaId) {
        Conta conta = contaRepository.findById(contaId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrado"));
        conta.setAtivo(false);
        contaRepository.save(conta);
    }

    public void enableContaById(Long contaId) {
        Conta conta = contaRepository.findById(contaId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrado"));
        conta.setAtivo(true);
        contaRepository.save(conta);
    }

    public List<Comunicacao> buscarHistoricoPorConta(Long contaId) {
        return comunicacaoRepository.findByContaId(contaId);
    }
}