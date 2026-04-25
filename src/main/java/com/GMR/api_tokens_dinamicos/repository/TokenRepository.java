package com.GMR.api_tokens_dinamicos.repository;

import com.GMR.api_tokens_dinamicos.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Interface de abstração do banco de dados (Repository Pattern).
 * O Spring Data JPA implementa automaticamente os comandos SQL necessários.
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    /**
     * Metod0 customizado (Query Method) para buscar um token específico no banco.
     * Equivalente a: SELECT * FROM table_tokens WHERE codigo = ? AND conta_id = ? AND status = ?
     */
    Optional<Token> findByCodigoAndContaIdAndStatus(String codigo, Long contaId, Token.StatusToken status);
}
