package com.msa.customer.core.domain.exceptions.business;

import java.util.UUID;

public class CustomerNotActiveException extends BusinessException {

    public CustomerNotActiveException(UUID customerId) {
        super("CUSTOMER_NOT_ACTIVE", "Customer " + customerId + " is not active");
    }
}
