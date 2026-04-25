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

    // Chave primária utilizando a classe Wrapper Long para evitar conflitos de persistência no Spring Data
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    // Código de 6 dígitos gerado criptograficamente
    @Column(nullable = false, length = 6)
    private String codigo;

    // Define o Tempo de Vida (TTL) do token para prevenção de falhas de segurança
    @Column(nullable = false, name = "data_expiracao")
    private LocalDateTime dataExpiracao;

    // Estado atual do token, mapeado como String no banco para facilitar leitura
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusToken status;

    // Relacionamento com a entidade Conta. O carregamento LAZY otimiza a performance das consultas.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    // Registra o canal utilizado (SMS, EMAIL, LIGACAO) para manter o histórico omnichannel
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "tipo_comunicacao")
    private TipoComunicacao tipoComunicacao;

    // Construtor vazio exigido pelo framework JPA
    public Token() {
    }

    /**
     * Construtor principal da regra de negócio.
     * Aplica o conceito de encapsulamento para garantir que
     * todos os tokens nasçam com o status ATIVO e com 5 minutos de validade.
     */
    public Token(String codigo, Conta conta, TipoComunicacao tipoComunicacao) {
        this.tipoComunicacao = tipoComunicacao;
        this.codigo = codigo;
        this.conta = conta;
        this.status = StatusToken.ATIVO;
        this.dataExpiracao = LocalDateTime.now().plusMinutes(5);
    }

    // Enumerações para controle de estado
    public enum StatusToken {
        ATIVO,
        USADO,
        EXPIRADO
    }

    // Enumerações para controle de canal de comunicação
    public enum TipoComunicacao {
        SMS,
        EMAIL,
        LIGACAO
    }

    // Getters
    public Conta getConta() {
        return conta;
    }
    public StatusToken getStatus() {
        return status;
    }
    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }
    public String getCodigo() {
        return codigo;
    }
    public Long getTokenId() {
        return tokenId;
    }

    // Único Setter liberado, pois o status é a única propriedade mutável no ciclo de vida do token (ex: de ATIVO para USADO)
    public void setStatus(StatusToken status) {
        this.status = status;
    }

}
