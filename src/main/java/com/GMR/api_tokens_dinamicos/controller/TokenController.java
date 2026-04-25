package com.GMR.api_tokens_dinamicos.controller;

import com.GMR.api_tokens_dinamicos.dto.TokenRequestDTO;
import com.GMR.api_tokens_dinamicos.dto.TokenValidationDTO;
import com.GMR.api_tokens_dinamicos.model.Token;
import com.GMR.api_tokens_dinamicos.service.TokenService;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Token> gerarToken(@RequestBody TokenRequestDTO request) {

        // Valida se a conta informada existe antes de prosseguir com a geração
        return contaRepository.findById(request.contaId())
                .map(conta->{
            Token token = tokenService.gerarTokenParaComunicacao(conta, request.destino(), request.tipo());
            return(ResponseEntity.status(HttpStatus.CREATED).body(token));
        })
        .orElse(ResponseEntity.notFound().build()); // Retorna 404 se a conta não existir
    }

    /**
     * Endpoint responsável por validar a autenticidade do código inserido pelo usuário.
     * Retorna HTTP 200 (OK) para sucesso e HTTP 401 (Unauthorized) para fraude ou expiração.
     */
    @PostMapping("/validar")
    public ResponseEntity<String> validarToken(@RequestBody TokenValidationDTO request) {
        boolean eValido = tokenService.validarToken(request.codigo(), request.contaId());

        if (eValido) {
            return ResponseEntity.ok("Token validado com sucesso. Acesso liberado.");
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token já utilizado, inválido ou expirado.");
        }
    }
}
