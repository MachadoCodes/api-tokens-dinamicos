package com.GMR.api_tokens_dinamicos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GMR.api_tokens_dinamicos.model.Conta;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Optional<Conta> findByAgenciaAndNumeroConta(String agencia, String numeroConta);

    Optional<Conta> findByIdAndAtivoTrue(Long Id);
    List<Conta> findByUsuarioIdAndAtivoTrue(Long usuarioid);
}