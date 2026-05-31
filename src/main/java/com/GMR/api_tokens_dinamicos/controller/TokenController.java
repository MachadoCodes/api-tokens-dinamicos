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

/**
 * Controlador REST que expõe os endpoints de geração e validação de tokens via requisições HTTP.
 */
@RestController
@RequestMapping("/api/v1/tokens") // Versionamento da API
public class TokenController {

    private final TokenService tokenService;
    private final ContaRepository contaRepository;

    public TokenController(TokenService tokenService, ContaRepository contaRepository) {
        this.tokenService = tokenService;
        this.contaRepository = contaRepository;
    }

    /**
     * Endpoint responsável por gerar e disparar o token.
     * Retorna HTTP 201 (Created) em caso de sucesso.
     */
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

    /**
     * Endpoint responsável por validar a autenticidade do código inserido pelo usuário.
     * Retorna HTTP 200 (OK) para sucesso e HTTP 400 (Bad Request) com JSON de erro para falhas.
     */
    @PostMapping("/validar")
    public ResponseEntity<?> validarToken(@Valid @RequestBody TokenValidationDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String numeroContaDoJwt = authentication.getName();

        try {
            // Tenta validar. Se o TokenService encontrar fraude/expiração, ele interrompe e pula pro catch
            Token tokenValidado = tokenService.validarTokenSeguro(request.codigo(), numeroContaDoJwt);

            // Se passou direto, monta o JSON de sucesso
            Map<String, String> resposta = new HashMap<>();
            resposta.put("mensagem", "Autenticidade confirmada! A comunicação recebida é legítima e foi enviada por nossa instituição.");
            resposta.put("tipoCanal", tokenValidado.getTipoComunicacao().name());

            return ResponseEntity.ok(resposta);

        } catch (IllegalArgumentException e) {
            // Captura a mensagem de segurança exata que criamos no TokenService e manda como JSON
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

        // Mapeia as entidades para o DTO revertendo o TTL dinâmico corretamente
        java.util.List<TokenHistoricoDTO> historico = tokens.stream().map(t -> {

            // Corrige o cálculo da data de geração baseando-se no canal
            LocalDateTime dataGeracao;
            if (t.getTipoComunicacao() == Token.TipoComunicacao.LIGACAO) {
                dataGeracao = t.getDataExpiracao().minusMinutes(30);
            } else {
                dataGeracao = t.getDataExpiracao().minusHours(24);
            }

            return new TokenHistoricoDTO(
                    t.getCodigo(),
                    t.getTipoComunicacao().name(),
                    dataGeracao,
                    t.getStatus().name()
            );
        }).toList();

        return ResponseEntity.ok(historico);
    }
}