package com.GMR.api_tokens_dinamicos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.GMR.api_tokens_dinamicos.model.Usuario;
import com.GMR.api_tokens_dinamicos.dto.UsuarioRequestDTO;
import com.GMR.api_tokens_dinamicos.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Resolvendo o "Field injection is not recommended" com Injeção via Construtor
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> findAll(){
        return usuarioRepository.findByAtivoTrue();
    }

    public Optional<Usuario> findUsuarioById(Long id){
        return usuarioRepository.findByIdAndAtivoTrue(id);
    }

    public Optional<Usuario> findByCpf(String cpf){
        return usuarioRepository.findByCpf(cpf);
    }

    public Optional<Usuario> findByEmail(String email){
        return usuarioRepository.findByEmail(email);
    }

    public Usuario saveUsuario(UsuarioRequestDTO dto){
        Usuario novoUsuario = new Usuario();

        novoUsuario.setNomeUsuario(dto.nomeUsuario());
        novoUsuario.setCpf(dto.cpf());
        novoUsuario.setEmail(dto.email());
        novoUsuario.setTelefoneFixo(dto.telefoneFixo());
        novoUsuario.setTelefoneCelular(dto.telefoneCelular());
        novoUsuario.setAtivo(true);

        return usuarioRepository.save(novoUsuario);
    }

    public void disableUsuarioById(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public void enableUsuarioById(Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrado"));

        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    public Usuario updateUsuarioById(Long id, UsuarioRequestDTO dadosNovos){
        return findUsuarioById(id).map(usuarioExistente -> {
            usuarioExistente.setNomeUsuario(dadosNovos.nomeUsuario());
            usuarioExistente.setCpf(dadosNovos.cpf());
            usuarioExistente.setEmail(dadosNovos.email());
            usuarioExistente.setTelefoneFixo(dadosNovos.telefoneFixo());
            usuarioExistente.setTelefoneCelular(dadosNovos.telefoneCelular());

            return usuarioRepository.save(usuarioExistente);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario não encontrado para o ID:" + id));
    }
}