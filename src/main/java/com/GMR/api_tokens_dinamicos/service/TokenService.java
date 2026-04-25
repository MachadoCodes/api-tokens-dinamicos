package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Token;
import com.GMR.api_tokens_dinamicos.repository.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Classe responsável por orquestrar a lógica de negócio principal do sistema de tokens.
 */
@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final MensageriaService mensageriaService;
    private final SecureRandom secureRandom;

    // Injeção de dependências via construtor (Boas práticas do Spring)
    public TokenService(TokenRepository tokenRepository, MensageriaService mensageriaService) {
        this.tokenRepository = tokenRepository;
        this.mensageriaService = mensageriaService;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Gera um novo token, salva no banco e aciona o simulador de mensageria.
     * A anotação @Transactional garante o rollback do banco caso o envio falhe (Propriedade ACID).
     */
    @Transactional
    public Token gerarTokenParaComunicacao(Conta conta, String destino, Token.TipoComunicacao tipo) {
        // Gera um código numérico de 6 dígitos preenchido com zeros à esquerda
        String codigoGerado = String.format("%06d", secureRandom.nextInt(1000000));

        Token novoToken = new Token(codigoGerado, conta, tipo);
        tokenRepository.save(novoToken);// Persiste na base de dados

        // Delega o envio da mensagem para o serviço de mensageria (Mock)
        mensageriaService.enviarComunicacao(destino, codigoGerado, tipo);
        return novoToken;
    }

    /**
     * Valida a autenticidade de um token fornecido pelo usuário.
     * Retorna true se o token for válido e não estiver expirado.
     */
    @Transactional
    public boolean validarToken(String codigoFornecido, Long contaId) {

        // Busca o token ativo correspondente à conta e ao código informados
        Optional<Token> tokenOpt = tokenRepository.findByCodigoAndContaIdAndStatus(codigoFornecido, contaId, Token.StatusToken.ATIVO);

        if (tokenOpt.isEmpty()) {
            return false; // Falha: Token não encontrado, pertence a outra conta ou já está inativo
        }

        Token token = tokenOpt.get();

        // Validação rigorosa do Tempo de Vida (TTL)
        if (LocalDateTime.now().isAfter(token.getDataExpiracao())) {
            token.setStatus(Token.StatusToken.EXPIRADO);
            tokenRepository.save(token);
            return false;
        }

        // Sucesso na validação. Inativa o token para prevenir Ataques de Repetição (Replay Attacks).
        token.setStatus(Token.StatusToken.USADO);
        tokenRepository.save(token);
        return true;
    }
}