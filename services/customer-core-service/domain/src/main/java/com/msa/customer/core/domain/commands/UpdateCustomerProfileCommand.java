package com.msa.customer.core.domain.commands;

import java.util.UUID;

/** Command: update the mutable profile data of an existing customer. */
public record UpdateCustomerProfileCommand(UUID customerId, String fullName, String phoneNumber, String countryCode) {
}
