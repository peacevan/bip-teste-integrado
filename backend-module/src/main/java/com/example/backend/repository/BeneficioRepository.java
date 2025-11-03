package com.example.backend.repository;

import com.example.backend.entity.BeneficioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BeneficioRepository extends JpaRepository<BeneficioEntity, Long> {
    
  
    List<BeneficioEntity> findByAtivoTrue();
    
   
    @Query("SELECT b FROM BeneficioEntity b WHERE LOWER(b.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<BeneficioEntity> findByNomeContainingIgnoreCase(String nome);
    
  
    long countByAtivoTrue();
}