package com.GMR.api_tokens_dinamicos.controller;

import com.GMR.api_tokens_dinamicos.dto.TokenRequestDTO;
import com.GMR.api_tokens_dinamicos.dto.TokenValidationDTO;
import com.GMR.api_tokens_dinamicos.model.Token;
import com.GMR.api_tokens_dinamicos.service.TokenService;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import com.GMR.api_tokens_dinamicos.dto.TokenHistoricoDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tokens")
public class TokenController {

    private final TokenService tokenService;
    private final ContaRepository contaRepository;

    public TokenController(TokenService tokenService, ContaRepository contaRepository) {
        this.tokenService = tokenService;
        this.contaRepository = contaRepository;
    }

    @PostMapping("/gerar")
    public ResponseEntity<Token> gerarToken(@Valid @RequestBody TokenRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String numeroContaDoJwt = authentication.getName();

        return contaRepository.findByNumeroConta(numeroContaDoJwt)
                .map(conta -> {
                    String destinoReal = "";

                    if (Token.TipoComunicacao.SMS.equals(request.tipo())) {
                        destinoReal = conta.getUsuario().getTelefoneCelular();
                        if (destinoReal != null && !destinoReal.startsWith("+")) {
                            destinoReal = "+55" + destinoReal;
                        }
                    } else if (Token.TipoComunicacao.EMAIL.equals(request.tipo())) {
                        destinoReal = conta.getUsuario().getEmail();
                    }

                    Token token = tokenService.gerarTokenParaComunicacao(conta, destinoReal, request.tipo());
                    return ResponseEntity.status(HttpStatus.CREATED).body(token);
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validarToken(@Valid @RequestBody TokenValidationDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String numeroContaDoJwt = authentication.getName();

        try {
            Token tokenValidado = tokenService.validarTokenSeguro(request.codigo(), numeroContaDoJwt);

            Map<String, String> resposta = new HashMap<>();
            resposta.put("mensagem", "Autenticidade confirmada! A comunicação recebida é legítima e foi enviada por nossa instituição.");
            resposta.put("tipoCanal", tokenValidado.getTipoComunicacao().name());

            return ResponseEntity.ok(resposta);

        } catch (IllegalArgumentException e) {
            Map<String, Object> erroResponse = new HashMap<>();
            erroResponse.put("status", HttpStatus.BAD_REQUEST.value());
            erroResponse.put("mensagem", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erroResponse);
        }
    }

    @GetMapping("/historico")
    public ResponseEntity<java.util.List<TokenHistoricoDTO>> listarHistorico() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String numeroConta = authentication.getName();

        java.util.List<Token> tokens = tokenService.buscarHistorico90Dias(numeroConta);

        java.util.List<TokenHistoricoDTO> historico = tokens.stream().map(t -> {

            LocalDateTime dataGeracao;
            String canalOrigem;
            String statusAtual = t.getStatus().name();

            // Mapeamento Inteligente: Tratando fraudes (Canal Nulo)
            if (t.getTipoComunicacao() == null) {
                dataGeracao = t.getDataExpiracao().minusHours(24);
                canalOrigem = "DESCONHECIDO (EXTERNO)";
            } else {
                canalOrigem = t.getTipoComunicacao().name();
                if (t.getTipoComunicacao() == Token.TipoComunicacao.LIGACAO) {
                    dataGeracao = t.getDataExpiracao().minusMinutes(30);
                } else {
                    dataGeracao = t.getDataExpiracao().minusHours(24);
                }
            }

            // Mapeamento Dinâmico: Verifica em tempo real se um token ATIVO já venceu por tempo
            if (t.getStatus() == Token.StatusToken.ATIVO && t.getDataExpiracao().isBefore(LocalDateTime.now())) {
                statusAtual = Token.StatusToken.EXPIRADO.name();
            }

            return new TokenHistoricoDTO(
                    t.getCodigo(),
                    canalOrigem,
                    dataGeracao,
                    statusAtual
            );
        }).toList();

        return ResponseEntity.ok(historico);
    }
}