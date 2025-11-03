package com.example.backend;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferRequest;
import com.example.backend.service.BeneficioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller para operações de Benefício
 * Integra Spring Boot com EJB Service
 */
@RestController
@RequestMapping("/api/v1/beneficios")
public class BeneficioController {

    @Autowired
    private BeneficioService beneficioService;

    @GetMapping
      public ResponseEntity<List<BeneficioResponse>> listAll() {
        List<BeneficioResponse> beneficios = beneficioService.findAll();
        return ResponseEntity.ok()
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(beneficios);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BeneficioResponse>> listActive() {
        List<BeneficioResponse> beneficios = beneficioService.findActive();
        return ResponseEntity.ok(beneficios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficioResponse> findById(@PathVariable Long id) {
        Optional<BeneficioResponse> beneficio = beneficioService.findById(id);
        return beneficio.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<BeneficioResponse>> searchByName(@RequestParam String nome) {
        List<BeneficioResponse> beneficios = beneficioService.findByName(nome);
        return ResponseEntity.ok(beneficios);
    }

    @PostMapping
    public ResponseEntity<BeneficioResponse> create(@RequestBody BeneficioRequest request) {
        BeneficioResponse created = beneficioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficioResponse> update(@PathVariable Long id, @RequestBody BeneficioRequest request) {
        Optional<BeneficioResponse> updated = beneficioService.update(id, request);
        return updated.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        boolean deactivated = beneficioService.deactivate(id);
        return deactivated ? ResponseEntity.noContent().build() 
                          : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = beneficioService.delete(id);
        return deleted ? ResponseEntity.noContent().build() 
                      : ResponseEntity.notFound().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        try {
            beneficioService.transfer(request);
            return ResponseEntity.ok("Transferência realizada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erro na transferência: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countActive() {
        long count = beneficioService.countActive();
        return ResponseEntity.ok(count);
    }
}
