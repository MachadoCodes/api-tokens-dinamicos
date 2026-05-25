package com.GMR.api_tokens_dinamicos.controller;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid; // <-- Importação crucial
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.GMR.api_tokens_dinamicos.model.Usuario;
import com.GMR.api_tokens_dinamicos.dto.UsuarioRequestDTO; // <-- DTO que precisaremos
import com.GMR.api_tokens_dinamicos.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Injeção de dependência via Construtor (Melhor prática atual do Spring)
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService; // Correção aplicada!
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.findUsuarioById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Usuario> findByCpf(@PathVariable String cpf) {
        Optional<Usuario> usuario = usuarioService.findByCpf(cpf);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> findByEmail(@PathVariable String email) {
        Optional<Usuario> usuario = usuarioService.findByEmail(email);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    // Note a presença do @Valid e do UsuarioRequestDTO
    public ResponseEntity<Usuario> createUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        Usuario usuarioSalvo = usuarioService.saveUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @PutMapping("/{id}")
    // Note a presença do @Valid e do UsuarioRequestDTO
    public ResponseEntity<Usuario> updateUsuarioById(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dadosNovos) {
        Usuario usuarioAtualizado = usuarioService.updateUsuarioById(id, dadosNovos);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuarioById(@PathVariable Long id) {
        usuarioService.disableUsuarioById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reactiveUsuarioById(@PathVariable Long id) {
        usuarioService.enableUsuarioById(id);
        return ResponseEntity.noContent().build();
    }
}