package com.GMR.api_tokens_dinamicos.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GMR.api_tokens_dinamicos.model.Usuario;
import com.GMR.api_tokens_dinamicos.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios(){
        List<Usuario> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id){
        Optional<Usuario> usuario = usuarioService.findUsuarioById(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Usuario> findByCpf(@PathVariable String cpf){
        Optional<Usuario> usuario = usuarioService.findByCpf(cpf);

        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> findByEmail(@PathVariable String email){
        Optional<Usuario> usuario = usuarioService.findByEmail(email);

        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario){
        Usuario usuarioSalvo = usuarioService.saveUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<Usuario> updateUsuarioById(@PathVariable Long id, @RequestBody Usuario dadosNovos){
        Usuario usuarioAtualizado = usuarioService.updateUsuarioById(id, dadosNovos);

        return ResponseEntity.ok(usuarioAtualizado);
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteUsuarioById(@PathVariable Long id){
        usuarioService.disableUsuarioById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping({"/{id}/reativar"})
    public ResponseEntity<Void> reactiveUsuarioById(@PathVariable Long id){
        usuarioService.enableUsuarioById(id);
        return ResponseEntity.noContent().build();
    }
}
