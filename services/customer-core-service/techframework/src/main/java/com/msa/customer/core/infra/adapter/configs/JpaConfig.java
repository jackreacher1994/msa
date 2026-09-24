package com.msa.customer.core.infra.adapter.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.msa.customer.core.infra.adapter.outbound.integration.repositories.springdatajpa")
@EntityScan(basePackages = "com.msa.customer.core.infra.adapter.outbound.integration.repositories.springdatajpa.entities")
public class JpaConfig {
}
