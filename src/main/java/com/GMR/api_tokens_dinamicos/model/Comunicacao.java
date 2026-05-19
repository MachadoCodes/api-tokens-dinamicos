package com.GMR.api_tokens_dinamicos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comunicacao")
public class Comunicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usaremos String para representar o Enum (SMS, EMAIL, PUSH) para simplificar o banco.
    @Column(length = 20, nullable = false)
    private String tipo;

    @Column(nullable = false)
    private LocalDateTime dataEnvio;

    @ManyToOne
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    @OneToOne
    @JoinColumn(name = "token_id", nullable = false)
    private Token token;

    // Construtor vazio exigido pelo JPA/Hibernate
    public Comunicacao() {

    }

    //getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
