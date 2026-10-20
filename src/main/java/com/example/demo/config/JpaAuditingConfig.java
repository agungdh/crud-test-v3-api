package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    /**
     * Placeholder until authentication is available.
     * Returning empty keeps the nullable created_by/updated_by/deleted_by columns empty.
     */
    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> Optional.empty();
    }
}
