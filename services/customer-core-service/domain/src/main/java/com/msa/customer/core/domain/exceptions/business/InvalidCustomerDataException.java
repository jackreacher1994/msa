package com.msa.customer.core.domain.exceptions.business;

public class InvalidCustomerDataException extends BusinessException {

    public InvalidCustomerDataException(String message) {
        super("CUSTOMER_INVALID_DATA", message);
    }
}
