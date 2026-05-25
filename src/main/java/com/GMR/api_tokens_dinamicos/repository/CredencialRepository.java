package com.GMR.api_tokens_dinamicos.repository;

import com.GMR.api_tokens_dinamicos.model.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredencialRepository extends JpaRepository<Credencial, Long> {

    Optional<Credencial> findByContaId(Long contaId);
}
