package com.GMR.api_tokens_dinamicos.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa o Token Dinâmico.
 * Mapeada para a tabela "table_tokens" no PostgreSQL.
 */
@Entity
@Table(name = "table_tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @Column(nullable = false, length = 6)
    private String codigo;

    @Column(nullable = false, name = "data_expiracao")
    private LocalDateTime dataExpiracao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusToken status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    // AVISO: O 'nullable = false' foi removido para permitir a auditoria de tokens suspeitos sem canal oficial
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comunicacao")
    private TipoComunicacao tipoComunicacao;

    public Token() {
    }

    public Token(String codigo, Conta conta, TipoComunicacao tipoComunicacao) {
        this.tipoComunicacao = tipoComunicacao;
        this.codigo = codigo;
        this.conta = conta;
        this.status = StatusToken.ATIVO;

        if (tipoComunicacao == TipoComunicacao.LIGACAO) {
            this.dataExpiracao = LocalDateTime.now().plusMinutes(30);
        } else {
            this.dataExpiracao = LocalDateTime.now().plusHours(24);
        }
    }

    // Enumerações para controle de estado (Agora com SUSPEITO)
    public enum StatusToken {
        ATIVO,
        USADO,
        EXPIRADO,
        SUSPEITO
    }

    // Enumerações para controle de canal de comunicação
    public enum TipoComunicacao {
        SMS,
        EMAIL,
        LIGACAO
    }

    // Getters
    public Conta getConta() { return conta; }
    public StatusToken getStatus() { return status; }
    public LocalDateTime getDataExpiracao() { return dataExpiracao; }
    public String getCodigo() { return codigo; }
    public Long getTokenId() { return tokenId; }
    public TipoComunicacao getTipoComunicacao() { return tipoComunicacao; }

    // Setters necessários para a criação do registro de auditoria e controle de ciclo de vida
    public void setStatus(StatusToken status) { this.status = status; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setConta(Conta conta) { this.conta = conta; }
    public void setDataExpiracao(LocalDateTime dataExpiracao) { this.dataExpiracao = dataExpiracao; }
    public void setTipoComunicacao(TipoComunicacao tipoComunicacao) { this.tipoComunicacao = tipoComunicacao; }
}