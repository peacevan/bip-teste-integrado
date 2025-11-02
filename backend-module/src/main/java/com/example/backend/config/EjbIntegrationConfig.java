package com.example.backend.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuração para integrar lógica do EJB com Spring Boot
 * O SpringBootEjbAdapter já é um @Service, então não precisa de configuração adicional
 */
@Configuration
public class EjbIntegrationConfig {
    
    // Nenhuma configuração necessária - SpringBootEjbAdapter é @Service
    // Mantendo esta classe para futuras configurações de integração EJB
}