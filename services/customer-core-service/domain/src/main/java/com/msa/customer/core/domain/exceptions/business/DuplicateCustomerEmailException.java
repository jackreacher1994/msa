package com.msa.customer.core.domain.exceptions.business;

public class DuplicateCustomerEmailException extends BusinessException {

    public DuplicateCustomerEmailException(String email) {
        super("CUSTOMER_EMAIL_ALREADY_EXISTS", "A customer with email " + email + " already exists");
    }
}
