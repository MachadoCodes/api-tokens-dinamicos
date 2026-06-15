package com.GMR.api_tokens_dinamicos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.GMR.api_tokens_dinamicos.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCpf(String cpf);
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByAtivoTrue();
    Optional<Usuario> findByIdAndAtivoTrue(Long id);
}
