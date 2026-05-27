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
        // Valida se a conta informada existe antes de prosseguir com a geração
        return contaRepository.findById(request.contaId())
                .map(conta -> {
                    Token token = tokenService.gerarTokenParaComunicacao(conta, request.destino(), request.tipo());
                    return (ResponseEntity.status(HttpStatus.CREATED).body(token));
                })
                .orElse(ResponseEntity.notFound().build()); // Retorna 404 se a conta não existir
    }

    /**
     * Endpoint responsável por validar a autenticidade do código inserido pelo usuário.
     * Retorna HTTP 200 (OK) com os dados do token para sucesso e HTTP 401 (Unauthorized) para fraude ou expiração.
     */
    @PostMapping("/validar")
    // Mudança para ResponseEntity<?> para permitir retornar String ou Map dinamicamente
    public ResponseEntity<?> validarToken(@Valid @RequestBody TokenValidationDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String numeroContaDoJwt = authentication.getName();

        // Recebe o objeto Token completo (ou null) vindo do serviço
        Token tokenValidado = tokenService.validarTokenSeguro(request.codigo(), numeroContaDoJwt);

        if (tokenValidado != null) {
            // Cria a estrutura JSON para o front-end ler o canal real
            Map<String, String> resposta = new HashMap<>();
            resposta.put("mensagem", "Autenticidade confirmada! A comunicação recebida é legítima e foi enviada por nossa instituição.");
            resposta.put("tipoCanal", tokenValidado.getTipoComunicacao().name()); // SMS, EMAIL, LIGACAO

            return ResponseEntity.ok(resposta);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Atenção: Token inválido, expirado ou já utilizado. Cuidado com possíveis tentativas de fraude.");
        }
    }

    @GetMapping("/historico")
    public ResponseEntity<java.util.List<TokenHistoricoDTO>> listarHistorico() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String numeroConta = authentication.getName();

        java.util.List<Token> tokens = tokenService.buscarHistorico90Dias(numeroConta);

        // Mapeia as entidades para o DTO de forma limpa
        java.util.List<TokenHistoricoDTO> historico = tokens.stream().map(t -> new TokenHistoricoDTO(
                t.getCodigo(),
                t.getTipoComunicacao().name(),
                t.getDataExpiracao().minusMinutes(5), // Revertemos os 5 min do TTL para exibir a hora exata da geração na tela
                t.getStatus().name()
        )).toList();

        return ResponseEntity.ok(historico);
    }
}