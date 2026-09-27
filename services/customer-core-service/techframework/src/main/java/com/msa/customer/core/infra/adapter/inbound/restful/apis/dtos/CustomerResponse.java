package com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Matches the OpenAPI CustomerResponse schema. */
@RegisterForReflection
public record CustomerResponse(
        UUID id,
        String fullName,
        String email,
        String phoneNumber,
        String countryCode,
        String status,
        OffsetDateTime registeredAt,
        OffsetDateTime updatedAt) {
}
