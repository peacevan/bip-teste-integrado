package com.example.ejb;

import com.example.ejb.exception.InsufficientBalanceException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes TDD para BeneficioEjbService
 * Estes testes vão FALHAR inicialmente, expondo os bugs existentes
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BeneficioEjbService - Testes TDD")
class BeneficioEjbServiceTest {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BeneficioEjbService service;

    private Beneficio beneficioOrigem;
    private Beneficio beneficioDestino;

    @BeforeEach
    void setUp() {
        beneficioOrigem = new Beneficio("Beneficio A", "Descrição A", new BigDecimal("1000.00"));
        beneficioOrigem.setId(1L);
        
        beneficioDestino = new Beneficio("Beneficio B", "Descrição B", new BigDecimal("500.00"));
        beneficioDestino.setId(2L);
    }

    @Test
    @DisplayName("GREEN: Transfer com saldo suficiente deve funcionar")
    void transfer_WithSufficientBalance_ShouldSucceed() {
        // Arrange
        BigDecimal amount = new BigDecimal("300.00");
        when(entityManager.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioOrigem);
        when(entityManager.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioDestino);

        // Act
        assertDoesNotThrow(() -> service.transfer(1L, 2L, amount));

        // Assert
        assertEquals(new BigDecimal("700.00"), beneficioOrigem.getValor());
        assertEquals(new BigDecimal("800.00"), beneficioDestino.getValor());
        verify(entityManager).merge(beneficioOrigem);
        verify(entityManager).merge(beneficioDestino);
    }

    @Test
    @DisplayName("GREEN: Transfer com saldo insuficiente deve lançar exceção")
    void transfer_WithInsufficientBalance_ShouldThrowException() {
        // Arrange
        BigDecimal amount = new BigDecimal("1500.00"); // Maior que o saldo de 1000
        when(entityManager.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioOrigem);
        when(entityManager.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioDestino);

        // Act & Assert
        InsufficientBalanceException exception = assertThrows(
            InsufficientBalanceException.class,
            () -> service.transfer(1L, 2L, amount)
        );

        assertEquals(1L, exception.getBeneficioId());
        assertEquals(new BigDecimal("1000.00"), exception.getSaldoAtual());
        assertEquals(amount, exception.getValorTentativa());
        
        // Valores não devem ter mudado
        assertEquals(new BigDecimal("1000.00"), beneficioOrigem.getValor());
        assertEquals(new BigDecimal("500.00"), beneficioDestino.getValor());
    }

    @Test
    @DisplayName("GREEN: Transfer com valor zero deve lançar exceção")
    void transfer_WithZeroAmount_ShouldThrowException() {
        // Arrange
        BigDecimal amount = BigDecimal.ZERO;
        // Não precisa mock pois validação acontece antes da busca

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(1L, 2L, amount)
        );

        assertTrue(exception.getMessage().contains("deve ser maior que zero"));
    }

    @Test
    @DisplayName("GREEN: Transfer com valor negativo deve lançar exceção")
    void transfer_WithNegativeAmount_ShouldThrowException() {
        // Arrange
        BigDecimal amount = new BigDecimal("-100.00");
        // Não precisa mock pois validação acontece antes da busca

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(1L, 2L, amount)
        );

        assertTrue(exception.getMessage().contains("deve ser maior que zero"));
    }

    @Test
    @DisplayName("GREEN: Transfer para o mesmo benefício deve lançar exceção")
    void transfer_ToSameBeneficio_ShouldThrowException() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        // Não precisa mock pois validação acontece antes da busca

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(1L, 1L, amount)
        );

        assertTrue(exception.getMessage().contains("diferentes"));
    }

    @Test
    @DisplayName("GREEN: Transfer com benefício origem inexistente deve lançar exceção")
    void transfer_WithNonExistentFromBeneficio_ShouldThrowException() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(entityManager.find(Beneficio.class, 999L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(999L, 2L, amount)
        );

        assertTrue(exception.getMessage().contains("Benefício origem não encontrado"));
    }

    @Test
    @DisplayName("GREEN: Transfer com benefício destino inexistente deve lançar exceção")
    void transfer_WithNonExistentToBeneficio_ShouldThrowException() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(entityManager.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioOrigem);
        when(entityManager.find(Beneficio.class, 999L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.transfer(1L, 999L, amount)
        );

        assertTrue(exception.getMessage().contains("Benefício destino não encontrado"));
    }

    @Test
    @DisplayName("RED: Transferências concorrentes devem manter consistência")
    void transfer_ConcurrentTransfers_ShouldMaintainConsistency() throws Exception {
        // Este teste vai expor o problema de concorrência (race condition)
        
        // Arrange
        Beneficio beneficioA = new Beneficio("A", "Desc A", new BigDecimal("1000.00"));
        beneficioA.setId(1L);
        Beneficio beneficioB = new Beneficio("B", "Desc B", new BigDecimal("1000.00"));
        beneficioB.setId(2L);
        
        when(entityManager.find(Beneficio.class, 1L)).thenReturn(beneficioA);
        when(entityManager.find(Beneficio.class, 2L)).thenReturn(beneficioB);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Simular duas transferências simultâneas de 600 cada
        // Total inicial: A=1000, B=1000 (2000 total)
        // Após as operações: deve manter 2000 total
        
        CompletableFuture<Void> transfer1 = CompletableFuture.runAsync(() -> {
            try {
                service.transfer(1L, 2L, new BigDecimal("600.00"));
            } catch (Exception e) {
                // Pode falhar por saldo insuficiente, isso é esperado
            }
        }, executor);
        
        CompletableFuture<Void> transfer2 = CompletableFuture.runAsync(() -> {
            try {
                service.transfer(1L, 2L, new BigDecimal("600.00"));
            } catch (Exception e) {
                // Pode falhar por saldo insuficiente, isso é esperado
            }
        }, executor);

        // Wait for completion
        CompletableFuture.allOf(transfer1, transfer2).get(5, TimeUnit.SECONDS);
        executor.shutdown();

        // Assert - O total deve ser preservado
        BigDecimal totalFinal = beneficioA.getValor().add(beneficioB.getValor());
        assertEquals(new BigDecimal("2000.00"), totalFinal, 
            "Total deve ser preservado mesmo com operações concorrentes");
        
        // Pelo menos uma das operações deve ter falhado devido ao controle de concorrência
        assertTrue(beneficioA.getValor().compareTo(BigDecimal.ZERO) >= 0, 
            "Saldo não pode ficar negativo");
    }

    @Test
    @DisplayName("GREEN: Transfer deve usar locking otimista")
    void transfer_ShouldUseOptimisticLocking() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(entityManager.find(Beneficio.class, 1L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioOrigem);
        when(entityManager.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioDestino);
        
        // Simular conflito de versão
        when(entityManager.merge(any(Beneficio.class)))
            .thenThrow(new OptimisticLockException("Versão conflitante"));

        // Act & Assert
        assertThrows(OptimisticLockException.class, 
            () -> service.transfer(1L, 2L, amount));
    }

    @Test
    @DisplayName("GREEN: Transfer com saldo zero deve lançar exceção")
    void transfer_WithZeroBalance_ShouldThrowException() {
        // Arrange
        Beneficio beneficioSemSaldo = new Beneficio("Sem Saldo", "Saldo zerado", BigDecimal.ZERO);
        beneficioSemSaldo.setId(3L);
        
        BigDecimal amount = new BigDecimal("50.00");
        when(entityManager.find(Beneficio.class, 3L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioSemSaldo);
        when(entityManager.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioDestino);

        // Act & Assert
        InsufficientBalanceException exception = assertThrows(
            InsufficientBalanceException.class,
            () -> service.transfer(3L, 2L, amount)
        );

        assertEquals(3L, exception.getBeneficioId());
        assertEquals(BigDecimal.ZERO, exception.getSaldoAtual());
        assertEquals(amount, exception.getValorTentativa());
        
        // Saldo deve permanecer zero
        assertEquals(BigDecimal.ZERO, beneficioSemSaldo.getValor());
    }

    @Test
    @DisplayName("GREEN: Transfer com saldo exato deve funcionar")
    void transfer_WithExactBalance_ShouldSucceed() {
        // Arrange
        Beneficio beneficioExato = new Beneficio("Exato", "Saldo exato", new BigDecimal("100.00"));
        beneficioExato.setId(4L);
        
        BigDecimal amount = new BigDecimal("100.00"); // Valor exato do saldo
        when(entityManager.find(Beneficio.class, 4L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioExato);
        when(entityManager.find(Beneficio.class, 2L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(beneficioDestino);

        // Act
        assertDoesNotThrow(() -> service.transfer(4L, 2L, amount));

        // Assert
        assertEquals(0, beneficioExato.getValor().compareTo(BigDecimal.ZERO)); // Deve ficar com zero
        assertEquals(new BigDecimal("600.00"), beneficioDestino.getValor()); // 500 + 100
        verify(entityManager).merge(beneficioExato);
        verify(entityManager).merge(beneficioDestino);
    }
}