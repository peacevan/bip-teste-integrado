package com.example.ejb.exception;

/**
 * Exceção lançada quando uma transferência é tentada com saldo insuficiente
 */
public class InsufficientBalanceException extends RuntimeException {
    
    private final Long beneficioId;
    private final java.math.BigDecimal saldoAtual;
    private final java.math.BigDecimal valorTentativa;
    
    public InsufficientBalanceException(Long beneficioId, java.math.BigDecimal saldoAtual, java.math.BigDecimal valorTentativa) {
        super(String.format("Saldo insuficiente no benefício ID %d. Saldo atual: %s, Valor tentativa: %s", 
                beneficioId, saldoAtual, valorTentativa));
        this.beneficioId = beneficioId;
        this.saldoAtual = saldoAtual;
        this.valorTentativa = valorTentativa;
    }
    
    public Long getBeneficioId() {
        return beneficioId;
    }
    
    public java.math.BigDecimal getSaldoAtual() {
        return saldoAtual;
    }
    
    public java.math.BigDecimal getValorTentativa() {
        return valorTentativa;
    }
}