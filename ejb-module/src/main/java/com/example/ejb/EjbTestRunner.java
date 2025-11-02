package com.example.ejb;

import java.math.BigDecimal;

/**
 * Runner para testar o EJB diretamente
 * Simula um ambiente de execução para o BeneficioEjbService
 */
public class EjbTestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE DO MÓDULO EJB ===");
        System.out.println("Iniciando testes de transferência de benefícios...\n");
        
        // Instancia o serviço EJB
        BeneficioEjbService ejbService = new BeneficioEjbService();
        
        // Teste 1: Transferência válida
        System.out.println("📋 TESTE 1: Transferência Válida");
        try {
            ejbService.transfer(
                1L, // ID origem
                2L, // ID destino  
                new BigDecimal("100.00") // Valor
            );
            System.out.println("✅ Transferência realizada com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
        
        System.out.println();
        
        // Teste 2: ID inválido (null)
        System.out.println("📋 TESTE 2: ID Nulo");
        try {
            ejbService.transfer(
                null, // ID inválido
                2L,
                new BigDecimal("50.00")
            );
            System.out.println("✅ Transferência realizada com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro esperado: " + e.getMessage());
        }
        
        System.out.println();
        
        // Teste 3: Valor negativo
        System.out.println("📋 TESTE 3: Valor Negativo");
        try {
            ejbService.transfer(
                1L,
                2L,
                new BigDecimal("-10.00") // Valor negativo
            );
            System.out.println("✅ Transferência realizada com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro esperado: " + e.getMessage());
        }
        
        System.out.println();
        
        // Teste 4: Valor zero
        System.out.println("📋 TESTE 4: Valor Zero");
        try {
            ejbService.transfer(
                1L,
                2L,
                BigDecimal.ZERO
            );
            System.out.println("✅ Transferência realizada com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro esperado: " + e.getMessage());
        }
        
        System.out.println();
        
        // Teste 5: IDs iguais
        System.out.println("📋 TESTE 5: IDs Iguais (Transferência para si mesmo)");
        try {
            ejbService.transfer(
                1L,
                1L, // Mesmo ID
                new BigDecimal("25.00")
            );
            System.out.println("✅ Transferência realizada com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro esperado: " + e.getMessage());
        }
        
        System.out.println();
        
        // Teste 6: Valor muito alto (sem dados reais será erro de benefício não encontrado)
        System.out.println("📋 TESTE 6: Benefício Não Encontrado");
        try {
            ejbService.transfer(
                999L, // ID que não existe
                1000L,
                new BigDecimal("100.00")
            );
            System.out.println("✅ Transferência realizada com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro esperado: " + e.getMessage());
        }
        
        System.out.println("\n=== TESTES CONCLUÍDOS ===");
        System.out.println("EJB executado com sucesso!");
        System.out.println("Verifique os resultados acima para validar o comportamento.");
    }
}