package com.GMR.api_tokens_dinamicos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
 
@Service
public class ContaService {
    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioService usuarioService;

    public List<Conta> findAllById(Long usuarioId) {
        return contaRepository.findByUsuarioIdAndAtivoTrue(usuarioId);
    }

    public Optional<Conta> findContaById(Long contaId) {
        return contaRepository.findByIdAndAtivoTrue(contaId);
    }

    public Optional<Conta> findContaByAgenciaAndNumero(String agencia, String numeroDaConta){
        return contaRepository.findByAgenciaAndNumeroConta(agencia, numeroDaConta);
    }

    public Conta saveConta(Long usuarioId, Conta contaNova) {
        return usuarioService.findUsuarioById(usuarioId).map(usuarioEncontrado -> {
            usuarioEncontrado.adicionarConta(contaNova);
            return contaRepository.save(contaNova);

        }).orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + usuarioId));
    }

    

    public void disableContaById(Long contaId) {
        Conta conta = contaRepository.findById(contaId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrado"));

        conta.setAtivo(false);
        contaRepository.save(conta);
    }

    public void enableCartaoById(Long contaId) {
        Conta conta = contaRepository.findById(contaId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrado"));

        conta.setAtivo(true);
        contaRepository.save(conta);
    }

    
    // public Conta updateCartaoById(Long contaId, Cartao dadosNovos){
    //     return findContaById(contaId).map(cartaoExistente -> {
    //         cartaoExistente.setSaldo(dadosNovos.getSaldo());
    //         cartaoExistente.setTipo(dadosNovos.getTipo());
    //         return contaRepository.save(cartaoExistente);
    //     }).orElseThrow(() -> new RuntimeException("Cartão não encontrado com ID: " + contaId));
    // }
}
