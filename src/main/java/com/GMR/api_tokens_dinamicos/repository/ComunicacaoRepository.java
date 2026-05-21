package com.GMR.api_tokens_dinamicos.repository;

import com.GMR.api_tokens_dinamicos.model.Comunicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComunicacaoRepository extends JpaRepository<Comunicacao, Long> {

    // Mé-to-do do Spring Data JPA que busca to-do o histórico de uma conta.
    List<Comunicacao> findByContaId(long contaId);
}
