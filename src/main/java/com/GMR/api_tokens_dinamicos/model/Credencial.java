package com.GMR.api_tokens_dinamicos.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "credencial")

public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "conta_id", nullable = false, unique = true)
    private Conta conta;

    @Column(name = "hash_senha", nullable = false, unique = true)
    private String hashSenha;

    @Column(name = "tentativas_falhas", nullable = false)
    private Integer tentativasFalhas = 0;

    @Column(name = "data_ultima_alteracao")
    private LocalDateTime dataUltimaAlteracao;

    public Credencial() {
    }

    public Credencial(Conta conta, String hashSenha) {
        this.conta = conta;
        this.hashSenha = hashSenha;
        this.tentativasFalhas = 0;
        this.dataUltimaAlteracao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public String getHashSenha() {
        return hashSenha;
    }

    public void setHashSenha(String hashSenha) {
        this.hashSenha = hashSenha;
    }

    public Integer getTentativasFalhas() {
        return tentativasFalhas;
    }

    public void setTentativasFalhas(Integer tentativasFalhas) {
        this.tentativasFalhas = tentativasFalhas;
    }

    public LocalDateTime getDataUltimaAlteracao() {
        return dataUltimaAlteracao;
    }

    public void setDataUltimaAlteracao(LocalDateTime dataUltimaAlteracao) {
        this.dataUltimaAlteracao = dataUltimaAlteracao;
    }
}
