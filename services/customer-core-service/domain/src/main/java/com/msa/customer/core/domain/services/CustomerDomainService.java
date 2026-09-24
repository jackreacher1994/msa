package com.msa.customer.core.domain.services;

import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.exceptions.business.DuplicateCustomerEmailException;
import com.msa.customer.core.domain.valueobjects.EmailAddress;

import java.util.Optional;

/**
 * Domain service: business rules that span more than one aggregate instance and therefore do not
 * belong to a single {@link CustomerDomainEntity}. Stateless and free of infrastructure concerns;
 * the application layer supplies the data it needs.
 */
public class CustomerDomainService {

    /** Rule: an e-mail address identifies at most one customer. */
    public void assertEmailIsUnique(EmailAddress email, Optional<CustomerDomainEntity> existingCustomerWithEmail) {
        if (existingCustomerWithEmail.isPresent()) {
            throw new DuplicateCustomerEmailException(email.value());
        }
    }
}
