package com.GMR.api_tokens_dinamicos.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Atualizado para 150 caracteres, conforme o seu DER
    @Column(name = "nome_usuario", nullable = false, length = 150)
    private String nomeUsuario;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    // Atualizado para 255 caracteres, conforme o seu DER
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    // Adicionado telefone fixo (não é obrigatório/nullable)
    @Column(name = "telefone_fixo", length = 20)
    private String telefoneFixo;

    // Adicionado telefone celular (obrigatório e único)
    @Column(name = "telefone_celular", nullable = false, unique = true, length = 20)
    private String telefoneCelular;

    @Column(nullable = false)
    private boolean ativo = true;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Conta> contas;

    // Construtor Vazio para o JPA/Hibernate
    public Usuario() {
    }

    // Construtor atualizado com os novos campos de telefone
    public Usuario(String nomeUsuario, String cpf, String email, String telefoneFixo, String telefoneCelular) {
        this.nomeUsuario = nomeUsuario;
        this.cpf = cpf;
        this.email = email;
        this.telefoneFixo = telefoneFixo;
        this.telefoneCelular = telefoneCelular;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefoneFixo() {
        return telefoneFixo;
    }

    public void setTelefoneFixo(String telefoneFixo) {
        this.telefoneFixo = telefoneFixo;
    }

    public String getTelefoneCelular() {
        return telefoneCelular;
    }

    public void setTelefoneCelular(String telefoneCelular) {
        this.telefoneCelular = telefoneCelular;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public List<Conta> getContas() {
        return contas;
    }

    public void adicionarConta(Conta conta) {
        this.contas.add(conta);
        conta.setUsuario(this);
    }

    public void removerConta(Conta conta) {
        this.contas.remove(conta);
        conta.setUsuario(null);
    }
}