package com.msa.customer.core.domain.valueobjects;

import com.msa.customer.core.domain.constants.CustomerConstants;
import com.msa.customer.core.domain.utils.DomainAssertions;

import java.util.Locale;

/**
 * Value object: ISO 3166-1 alpha-2 country code. The format is a domain rule; whether the country is
 * currently supported is owned by the Reference Data bounded context (checked by the application layer).
 */
public record CountryCode(String value) {

    public CountryCode {
        DomainAssertions.notBlank(value, "countryCode");
        value = value.trim().toUpperCase(Locale.ROOT);
        DomainAssertions.matches(value, CustomerConstants.COUNTRY_CODE_PATTERN, "countryCode");
    }
}
