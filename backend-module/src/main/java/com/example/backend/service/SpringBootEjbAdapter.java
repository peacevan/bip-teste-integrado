package com.example.backend.service;

import com.example.backend.entity.BeneficioEntity;
import com.example.backend.repository.BeneficioRepository;
import com.example.ejb.exception.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Adaptador que implementa a lógica do EJB usando entidades do Spring Boot
 * Mantém toda a lógica de validação do EJB original
 */
@Service
@Transactional
public class SpringBootEjbAdapter {
    
    @Autowired
    private BeneficioRepository repository;
    
    /**
     * Implementa a mesma lógica de transferência do EJB
     * Usa as entidades e repositórios do Spring Boot
     */
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // 1. Validação de parâmetros (copiada do EJB)
        validateTransferParameters(fromId, toId, amount);
        
        // 2. Buscar benefícios
        BeneficioEntity from = findBeneficioWithValidation(fromId, "origem");
        BeneficioEntity to = findBeneficioWithValidation(toId, "destino");
        
        // 3. Validação de saldo suficiente
        validateSufficientBalance(from, amount);
        
        // 4. Realizar transferência
        performTransfer(from, to, amount);
        
        // 5. Persistir mudanças
        repository.save(from);
        repository.save(to);
    }
    
    /**
     * Validação de parâmetros (copiada do EJB)
     */
    private void validateTransferParameters(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null) {
            throw new IllegalArgumentException("ID do benefício origem não pode ser nulo");
        }
        if (toId == null) {
            throw new IllegalArgumentException("ID do benefício destino não pode ser nulo");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Valor da transferência não pode ser nulo");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser positivo");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Não é possível transferir para o mesmo benefício");
        }
    }
    
    /**
     * Busca benefício com validação (adaptada do EJB)
     */
    private BeneficioEntity findBeneficioWithValidation(Long id, String tipo) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Benefício " + tipo + " não encontrado com ID: " + id));
    }
    
    /**
     * Validação de saldo suficiente (adaptada para usar 'valor' ao invés de 'saldo')
     */
    private void validateSufficientBalance(BeneficioEntity from, BigDecimal amount) {
        if (from.getValor().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                from.getId(), 
                from.getValor(), 
                amount);
        }
    }
    
    /**
     * Executa a transferência (adaptada para usar 'valor' ao invés de 'saldo')
     */
    private void performTransfer(BeneficioEntity from, BeneficioEntity to, BigDecimal amount) {
        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));
    }
}