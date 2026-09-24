package com.msa.customer.core.domain.utils;

import com.msa.customer.core.domain.exceptions.business.InvalidCustomerDataException;

import java.util.regex.Pattern;

/** Guard clauses used by value objects to keep the domain always valid. */
public final class DomainAssertions {

    private DomainAssertions() {
    }

    public static void notBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidCustomerDataException(field + " must not be blank");
        }
    }

    public static void maxLength(String value, int max, String field) {
        if (value.length() > max) {
            throw new InvalidCustomerDataException(field + " must be at most " + max + " characters");
        }
    }

    public static void matches(String value, Pattern pattern, String field) {
        notBlank(value, field);
        if (!pattern.matcher(value.trim()).matches()) {
            throw new InvalidCustomerDataException(field + " has an invalid format");
        }
    }
}
