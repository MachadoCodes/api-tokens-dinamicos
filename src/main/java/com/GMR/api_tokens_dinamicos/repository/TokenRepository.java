package com.GMR.api_tokens_dinamicos.repository;

import com.GMR.api_tokens_dinamicos.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * Interface de abstração do banco de dados (Repository Pattern).
 * O Spring Data JPA implementa automaticamente os comandos SQL necessários.
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * [NOVO] Busca um token apenas pelo código e ID da conta.
     * Necessário para o Service auditar o status (USADO ou EXPIRADO) e retornar a mensagem correta.
     * Equivalente a: SELECT * FROM table_tokens WHERE codigo = ? AND conta_id = ?
     */
    Optional<Token> findByCodigoAndContaId(String codigo, Long contaId);

    /**
     * Metod0 customizado (Query Method) para buscar um token específico no banco.
     * Equivalente a: SELECT * FROM table_tokens WHERE codigo = ? AND conta_id = ? AND status = ?
     */
    Optional<Token> findByCodigoAndContaIdAndStatus(String codigo, Long contaId, Token.StatusToken status);

    // Traz os tokens de uma conta a partir de uma data, do mais novo para o mais velho
    List<Token> findByContaIdAndDataExpiracaoAfterOrderByDataExpiracaoDesc(Long contaId, java.time.LocalDateTime dataLimite);
}