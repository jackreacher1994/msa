package com.msa.customer.core.domain.valueobjects;

import com.msa.customer.core.domain.constants.CustomerConstants;
import com.msa.customer.core.domain.utils.DomainAssertions;

/** Value object: a customer's display name. */
public record FullName(String value) {

    public FullName {
        DomainAssertions.notBlank(value, "fullName");
        value = value.trim();
        DomainAssertions.maxLength(value, CustomerConstants.MAX_FULL_NAME_LENGTH, "fullName");
    }
}
