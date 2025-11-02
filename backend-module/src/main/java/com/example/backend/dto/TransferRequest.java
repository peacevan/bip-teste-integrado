package com.example.backend.dto;

import java.math.BigDecimal;

/**
 * DTO para requisições de transferência entre benefícios
 */
public class TransferRequest {
    
    private Long fromId;
    private Long toId;
    private BigDecimal amount;
    
    // Constructors
    public TransferRequest() {}
    
    public TransferRequest(Long fromId, Long toId, BigDecimal amount) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
    }
    
    // Getters and Setters
    public Long getFromId() {
        return fromId;
    }
    
    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }
    
    public Long getToId() {
        return toId;
    }
    
    public void setToId(Long toId) {
        this.toId = toId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    @Override
    public String toString() {
        return "TransferRequest{" +
                "fromId=" + fromId +
                ", toId=" + toId +
                ", amount=" + amount +
                '}';
    }
}