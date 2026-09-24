package com.msa.customer.core.domain.valueobjects;

import com.msa.customer.core.domain.constants.CustomerConstants;
import com.msa.customer.core.domain.utils.DomainAssertions;

import java.util.Locale;

/** Value object: immutable, self-validating, compared by value. Normalized to lower case. */
public record EmailAddress(String value) {

    public EmailAddress {
        DomainAssertions.matches(value, CustomerConstants.EMAIL_PATTERN, "email");
        value = value.trim().toLowerCase(Locale.ROOT);
    }
}
