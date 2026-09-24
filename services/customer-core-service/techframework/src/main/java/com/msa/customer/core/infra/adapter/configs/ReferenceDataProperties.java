package com.msa.customer.core.infra.adapter.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** Externalized configuration (12-factor) for the Reference Data service client. */
@ConfigurationProperties(prefix = "reference-data")
public record ReferenceDataProperties(String baseUrl, Duration connectTimeout, Duration readTimeout) {
}
