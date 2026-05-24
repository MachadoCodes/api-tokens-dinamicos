package com.GMR.api_tokens_dinamicos.controller;

import com.GMR.api_tokens_dinamicos.dto.TokenRequestDTO;
import com.GMR.api_tokens_dinamicos.dto.TokenValidationDTO;
import com.GMR.api_tokens_dinamicos.model.Token;
import com.GMR.api_tokens_dinamicos.service.TokenService;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import jakarta.validation.Valid; // <-- Importação do Valid
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    // Adicionado o @Valid para ativar as regras (NotBlank, Size, etc.) definidas no DTO
    public ResponseEntity<Token> gerarToken(@Valid @RequestBody TokenRequestDTO request) {

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
    // Adicionado o @Valid para proteger contra códigos com mais ou menos de 6 dígitos
    public ResponseEntity<String> validarToken(@Valid @RequestBody TokenValidationDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String numeroContaDoJwt = authentication.getName();

        boolean eValido = tokenService.validarTokenSeguro(request.codigo(), numeroContaDoJwt);

        if (eValido) {
            return ResponseEntity.ok("Autenticidade confirmada! A comunicação recebida é legítima e foi enviada por nossa instituição.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Atenção: Token inválido, expirado ou já utilizado. Cuidado com possíveis tentativas de fraude.");
        }
    }
}