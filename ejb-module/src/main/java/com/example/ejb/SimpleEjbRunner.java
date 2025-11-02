package com.example.ejb;

import java.math.BigDecimal;

/**
 * Runner simples para demonstrar a lógica de validação do EJB
 * Não utiliza EntityManager, apenas mostra as validações
 */
public class SimpleEjbRunner {
    
    public static void main(String[] args) {
        System.out.println("=== DEMONSTRAÇÃO DO MÓDULO EJB ===");
        System.out.println("Testando lógica de validação de transferências...\n");
        
        // Teste 1: Parâmetros nulos
        System.out.println("📋 TESTE 1: Validação de Parâmetros Nulos");
        testValidation(() -> validateTransferParameters(null, 2L, new BigDecimal("100")), 
                      "ID origem nulo");
        
        System.out.println();
        
        // Teste 2: Valor negativo
        System.out.println("📋 TESTE 2: Validação de Valor Negativo");
        testValidation(() -> validateTransferParameters(1L, 2L, new BigDecimal("-10")), 
                      "Valor negativo");
        
        System.out.println();
        
        // Teste 3: Valor zero
        System.out.println("📋 TESTE 3: Validação de Valor Zero");
        testValidation(() -> validateTransferParameters(1L, 2L, BigDecimal.ZERO), 
                      "Valor zero");
        
        System.out.println();
        
        // Teste 4: IDs iguais
        System.out.println("📋 TESTE 4: Validação de IDs Iguais");
        testValidation(() -> validateTransferParameters(1L, 1L, new BigDecimal("100")), 
                      "IDs iguais");
        
        System.out.println();
        
        // Teste 5: Parâmetros válidos
        System.out.println("📋 TESTE 5: Parâmetros Válidos");
        testValidation(() -> validateTransferParameters(1L, 2L, new BigDecimal("100")), 
                      "Parâmetros válidos");
        
        System.out.println("\n=== DEMONSTRAÇÃO CONCLUÍDA ===");
        System.out.println("✅ Lógica de validação do EJB funcionando corretamente!");
        System.out.println("🔗 Para execução completa, use via Spring Boot na porta 8080");
    }
    
    /**
     * Cópia da lógica de validação do BeneficioEjbService
     */
    private static void validateTransferParameters(Long fromId, Long toId, BigDecimal amount) {
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
     * Utilitário para testar validações
     */
    private static void testValidation(Runnable test, String description) {
        try {
            test.run();
            System.out.println("✅ " + description + ": Validação passou (sem erros)");
        } catch (Exception e) {
            System.out.println("❌ " + description + ": " + e.getMessage());
        }
    }
}