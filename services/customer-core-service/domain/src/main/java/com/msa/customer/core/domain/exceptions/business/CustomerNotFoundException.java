package com.msa.customer.core.domain.exceptions.business;

import java.util.UUID;

public class CustomerNotFoundException extends BusinessException {

    public CustomerNotFoundException(UUID customerId) {
        super("CUSTOMER_NOT_FOUND", "Customer " + customerId + " not found");
    }
}
