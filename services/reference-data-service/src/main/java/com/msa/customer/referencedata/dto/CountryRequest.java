package com.msa.customer.referencedata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CountryRequest(
        @NotBlank @Pattern(regexp = "^[A-Za-z]{2}$") String code,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Pattern(regexp = "^\\+[0-9]{1,4}$") String dialCode,
        boolean active) {
}
