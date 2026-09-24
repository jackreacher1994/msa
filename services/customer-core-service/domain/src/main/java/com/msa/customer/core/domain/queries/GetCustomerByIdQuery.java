package com.msa.customer.core.domain.queries;

import java.util.UUID;

/** Query (read-only intent, CQS): fetch one customer by identity. */
public record GetCustomerByIdQuery(UUID customerId) {
}
