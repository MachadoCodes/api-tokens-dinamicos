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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GMR.api_tokens_dinamicos.model.Conta;
import com.GMR.api_tokens_dinamicos.service.ContaService;

@RestController
@RequestMapping("/usuarios/{usuarioId}/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @GetMapping("/{contaId}")
    public ResponseEntity<Conta> getContaById(@PathVariable Long usuarioId, @PathVariable Long contaId) {
        Optional<Conta> conta = contaService.findContaById(contaId);
        return conta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Conta>> getAllContasByUsuarioId(@PathVariable Long usuarioId) {
        List<Conta> contas = contaService.findAllById(usuarioId);
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/busca")
    public ResponseEntity<Conta> findContaByAgenciaAndNumero(@RequestParam String agencia, @RequestParam String numeroConta){
        Optional<Conta> conta = contaService.findContaByAgenciaAndNumero(agencia, numeroConta);
        return conta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Conta> createConta(@PathVariable Long usuarioId, @RequestBody Conta contanova) {
        Conta contaSalva = contaService.saveConta(usuarioId, contanova);
        return ResponseEntity.status(HttpStatus.CREATED).body(contaSalva);
    }

    @DeleteMapping("/{contaId}")
    public ResponseEntity<Void> deleteContaById(@PathVariable Long usuarioId, @PathVariable Long contaId){
        contaService.disableContaById(contaId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{contaId}/reativar")
    public ResponseEntity<Void> reactiveContaById(@PathVariable Long usuarioId, @PathVariable Long contaId){
        contaService.enableCartaoById(contaId);
        return ResponseEntity.noContent().build();
    }
}
