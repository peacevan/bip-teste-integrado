package com.example.ejb;

import com.example.ejb.exception.InsufficientBalanceException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Realiza transferência entre benefícios com validações completas e controle de concorrência
     * 
     * @param fromId ID do benefício origem
     * @param toId ID do benefício destino  
     * @param amount Valor a ser transferido
     * @throws IllegalArgumentException para parâmetros inválidos
     * @throws InsufficientBalanceException quando saldo insuficiente
     */
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // 1. Validação de parâmetros
        validateTransferParameters(fromId, toId, amount);
        
        // 2. Buscar benefícios com locking pessimista para evitar race conditions
        Beneficio from = findBeneficioWithLock(fromId, "origem");
        Beneficio to = findBeneficioWithLock(toId, "destino");
        
        // 3. Validação de saldo suficiente
        validateSufficientBalance(from, amount);
        
        // 4. Realizar transferência
        performTransfer(from, to, amount);
        
        // 5. Persistir mudanças (com optimistic locking via @Version)
        em.merge(from);
        em.merge(to);
    }
    
    /**
     * Valida os parâmetros da transferência
     */
    private void validateTransferParameters(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null) {
            throw new IllegalArgumentException("ID do benefício origem não pode ser nulo");
        }
        
        if (toId == null) {
            throw new IllegalArgumentException("ID do benefício destino não pode ser nulo");
        }
        
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Benefícios origem e destino devem ser diferentes");
        }
        
        if (amount == null) {
            throw new IllegalArgumentException("Valor da transferência não pode ser nulo");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }
    }
    
    /**
     * Busca benefício com locking pessimista
     */
    private Beneficio findBeneficioWithLock(Long id, String tipo) {
        Beneficio beneficio = em.find(Beneficio.class, id, LockModeType.PESSIMISTIC_WRITE);
        
        if (beneficio == null) {
            throw new IllegalArgumentException("Benefício " + tipo + " não encontrado com ID: " + id);
        }
        
        if (!Boolean.TRUE.equals(beneficio.getAtivo())) {
            throw new IllegalArgumentException("Benefício " + tipo + " está inativo");
        }
        
        return beneficio;
    }
    
    /**
     * Valida se há saldo suficiente para a transferência
     * Saldo deve ser maior ou igual ao valor a ser transferido
     */
    private void validateSufficientBalance(Beneficio from, BigDecimal amount) {
        if (from.getValor().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(from.getId(), from.getValor(), amount);
        }
    }
    
    /**
     * Executa a transferência propriamente dita
     */
    private void performTransfer(Beneficio from, Beneficio to, BigDecimal amount) {
        // Subtrai do benefício origem
        BigDecimal newFromValue = from.getValor().subtract(amount);
        from.setValor(newFromValue);
        
        // Adiciona ao benefício destino
        BigDecimal newToValue = to.getValor().add(amount);
        to.setValor(newToValue);
    }
}
