package com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Matches the OpenAPI RegisterCustomerRequest schema (hand-written, native-safe). */
@RegisterForReflection
public record RegisterCustomerRequest(
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Email String email,
        @NotBlank String phoneNumber,
        @NotBlank @Size(min = 2, max = 2) String countryCode) {
}
