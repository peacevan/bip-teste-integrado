package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authorize -> authorize
                // Endpoints da API
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permitir OPTIONS para CORS
                .requestMatchers("/api/v1/**").permitAll()
                
                // Endpoints do Swagger/OpenAPI
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/v3/api-docs").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/api-docs").permitAll()
                
                // H2 Console
                .requestMatchers("/h2-console/**").permitAll()
                
                // Recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                
                // Outras requisições precisam de autenticação
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/v1/**")
                .ignoringRequestMatchers("/h2-console/**")
                .ignoringRequestMatchers("/swagger-ui/**")
                .ignoringRequestMatchers("/v3/api-docs/**")
                .ignoringRequestMatchers("/api-docs/**")
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin()  // Para permitir H2 Console
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200", "http://127.0.0.1:4200"));
        
       
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        
        configuration.setAllowCredentials(true);
        
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}