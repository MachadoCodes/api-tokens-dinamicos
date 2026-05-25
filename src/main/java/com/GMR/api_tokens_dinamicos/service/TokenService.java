package com.GMR.api_tokens_dinamicos.service;

import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.model.Token;
import com.GMR.api_tokens_dinamicos.repository.ContaRepository;
import com.GMR.api_tokens_dinamicos.repository.TokenRepository;
import com.GMR.api_tokens_dinamicos.model.Comunicacao;
import com.GMR.api_tokens_dinamicos.repository.ComunicacaoRepository;
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
    private final ContaRepository contaRepository;
    private final ComunicacaoRepository comunicacaoRepository;

    // Injeção de dependências via construtor (Boas práticas do Spring)
    public TokenService(TokenRepository tokenRepository, ContaRepository contaRepository,
                        MensageriaService mensageriaService, ComunicacaoRepository comunicacaoRepository) {
        this.tokenRepository = tokenRepository;
        this.contaRepository = contaRepository;
        this.mensageriaService = mensageriaService;
        this.comunicacaoRepository = comunicacaoRepository;
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
        tokenRepository.save(novoToken); // Persiste na base de dados

        // Delega o envio da mensagem para o serviço de mensageria (Mock)
        mensageriaService.enviarComunicacao(destino, codigoGerado, tipo);

        Comunicacao comunicacao = new Comunicacao();
        comunicacao.setTipo(tipo.name()); // Transforma o Enum em texto (Ex: "EMAIL" ou "SMS")
        comunicacao.setDataEnvio(LocalDateTime.now());
        comunicacao.setConta(conta);
        comunicacao.setToken(novoToken); // Vincula ao token que acabamos de salvar
        comunicacaoRepository.save(comunicacao);

        return novoToken;
    }

    /**
     * Valida a autenticidade de um token usando a identidade blindada extraída do JWT.
     * Retorna true se o token for válido e não estiver expirado.
     */
    @Transactional
    public boolean validarTokenSeguro(String codigoFornecido, String numeroConta) {

        // 1. Acha a conta usando o número blindado do JWT (como é Unique no banco, retorna só ela)
        Optional<Conta> contaOpt = contaRepository.findByNumeroConta(numeroConta);
        if (contaOpt.isEmpty()) {
            return false; // Se a conta não existir, falha a validação na hora
        }

        Conta contaDoJwt = contaOpt.get();

        // 2. Busca o token ativo correspondente à conta (usando o ID seguro) e ao código informados
        Optional<Token> tokenOpt = tokenRepository.findByCodigoAndContaIdAndStatus(
                codigoFornecido,
                contaDoJwt.getId(),
                Token.StatusToken.ATIVO
        );

        if (tokenOpt.isEmpty()) {
            return false; // Falha: Token não encontrado, pertence a outra conta ou já está inativo
        }

        Token token = tokenOpt.get();

        // 3. Validação rigorosa do Tempo de Vida (TTL)
        if (LocalDateTime.now().isAfter(token.getDataExpiracao())) {
            token.setStatus(Token.StatusToken.EXPIRADO);
            tokenRepository.save(token);
            return false;
        }

        // 4. Sucesso na validação. Inativa o token para prevenir Ataques de Repetição (Regra do Descarte original de vocês!).
        token.setStatus(Token.StatusToken.USADO);
        tokenRepository.save(token);
        return true;
    }
}