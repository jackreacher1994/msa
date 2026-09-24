package com.msa.customer.core.domain.valueobjects;

import com.msa.customer.core.domain.constants.CustomerConstants;
import com.msa.customer.core.domain.utils.DomainAssertions;

/** Value object: phone number in E.164 format, e.g. +84901234567. */
public record PhoneNumber(String value) {

    public PhoneNumber {
        DomainAssertions.matches(value, CustomerConstants.PHONE_PATTERN, "phoneNumber");
        value = value.trim();
    }
}
