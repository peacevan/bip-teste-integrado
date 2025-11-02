package com.example.backend.service;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferRequest;
import com.example.backend.entity.BeneficioEntity;
import com.example.backend.repository.BeneficioRepository;
import com.example.ejb.exception.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service que integra Spring Boot com EJB
 * - CRUD operations via Spring Data JPA
 * - Business operations via EJB Service
 */
@Service
@Transactional
public class BeneficioService {
    
    @Autowired
    private BeneficioRepository repository;
    
    @Autowired
    private SpringBootEjbAdapter ejbAdapter;
    
    /**
     * Lista todos os benefícios
     */
    @Transactional(readOnly = true)
    public List<BeneficioResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lista benefícios ativos
     */
    @Transactional(readOnly = true)
    public List<BeneficioResponse> findActive() {
        return repository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca benefício por ID
     */
    @Transactional(readOnly = true)
    public Optional<BeneficioResponse> findById(Long id) {
        return repository.findById(id)
                .map(this::toResponse);
    }
    
    /**
     * Busca benefícios por nome
     */
    @Transactional(readOnly = true)
    public List<BeneficioResponse> findByName(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Cria novo benefício
     */
    public BeneficioResponse create(BeneficioRequest request) {
        BeneficioEntity entity = new BeneficioEntity();
        entity.setNome(request.getNome());
        entity.setDescricao(request.getDescricao());
        entity.setValor(request.getValor());
        entity.setAtivo(request.getAtivo());
        
        BeneficioEntity saved = repository.save(entity);
        return toResponse(saved);
    }
    
    /**
     * Atualiza benefício existente
     */
    public Optional<BeneficioResponse> update(Long id, BeneficioRequest request) {
        return repository.findById(id)
                .map(entity -> {
                    entity.setNome(request.getNome());
                    entity.setDescricao(request.getDescricao());
                    entity.setValor(request.getValor());
                    entity.setAtivo(request.getAtivo());
                    return toResponse(repository.save(entity));
                });
    }
    
    /**
     * Inativa benefício (soft delete)
     */
    public boolean deactivate(Long id) {
        return repository.findById(id)
                .map(entity -> {
                    entity.setAtivo(false);
                    repository.save(entity);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Remove benefício permanentemente
     */
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Transfere valores entre benefícios usando lógica do EJB adaptada para Spring Boot
     */
    public void transfer(TransferRequest request) {
        try {
            ejbAdapter.transfer(request.getFromId(), request.getToId(), request.getAmount());
        } catch (InsufficientBalanceException e) {
            throw new RuntimeException("Saldo insuficiente: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Parâmetros inválidos: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro na transferência: " + e.getMessage(), e);
        }
    }
    
    /**
     * Conta benefícios ativos
     */
    @Transactional(readOnly = true)
    public long countActive() {
        return repository.countByAtivoTrue();
    }
    
    /**
     * Converte Entity para Response DTO
     */
    private BeneficioResponse toResponse(BeneficioEntity entity) {
        return new BeneficioResponse(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getValor(),
                entity.getAtivo(),
                entity.getVersion()
        );
    }
}