package com.GMR.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GMR.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Object> {
    Optional<Usuario> findByCpf(String cpf);
    Optional<Usuario> findByEmail(String email);
}
