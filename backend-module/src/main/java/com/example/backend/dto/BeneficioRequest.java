package com.example.backend.dto;

import java.math.BigDecimal;

/**
 * DTO para criação/atualização de benefício
 */
public class BeneficioRequest {
    
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private Boolean ativo = true;
    
    // Constructors
    public BeneficioRequest() {}
    
    public BeneficioRequest(String nome, String descricao, BigDecimal valor) {
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "BeneficioRequest{" +
                "nome='" + nome + '\'' +
                ", valor=" + valor +
                ", ativo=" + ativo +
                '}';
    }
}