package com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RegisterForReflection
public record UpdateCustomerProfileRequest(
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank String phoneNumber,
        @NotBlank @Size(min = 2, max = 2) String countryCode) {
}
