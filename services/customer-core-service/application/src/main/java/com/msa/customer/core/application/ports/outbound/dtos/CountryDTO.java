package com.msa.customer.core.application.ports.outbound.dtos;

/**
 * Data received from the Reference Data bounded context. Kept as a DTO at the port boundary so the
 * supplier's model never leaks into our domain (a minimal anti-corruption layer).
 */
public record CountryDTO(String code, String name, boolean active) {
}
