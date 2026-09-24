package com.msa.customer.core.domain.commands;

/** Command (intent to change state): register a new customer. Carries raw input; the aggregate validates it. */
public record RegisterCustomerCommand(String fullName, String email, String phoneNumber, String countryCode) {
}
