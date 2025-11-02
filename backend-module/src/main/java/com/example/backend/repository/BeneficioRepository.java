package com.example.backend.repository;

import com.example.backend.entity.BeneficioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações CRUD de Beneficio
 */
@Repository
public interface BeneficioRepository extends JpaRepository<BeneficioEntity, Long> {
    
    /**
     * Busca benefícios ativos
     */
    List<BeneficioEntity> findByAtivoTrue();
    
    /**
     * Busca benefícios por nome (case insensitive)
     */
    @Query("SELECT b FROM BeneficioEntity b WHERE LOWER(b.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<BeneficioEntity> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Conta benefícios ativos
     */
    long countByAtivoTrue();
}