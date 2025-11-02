package com.example.backend.dto;

import java.math.BigDecimal;

/**
 * DTO para resposta de benefício
 */
public class BeneficioResponse {
    
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private Boolean ativo;
    private Long version;
    
    // Constructors
    public BeneficioResponse() {}
    
    public BeneficioResponse(Long id, String nome, String descricao, BigDecimal valor, Boolean ativo, Long version) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.ativo = ativo;
        this.version = version;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public void setVersion(Long version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        return "BeneficioResponse{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", valor=" + valor +
                ", ativo=" + ativo +
                '}';
    }
}