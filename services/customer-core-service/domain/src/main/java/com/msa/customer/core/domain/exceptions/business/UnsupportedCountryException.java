package com.msa.customer.core.domain.exceptions.business;

public class UnsupportedCountryException extends BusinessException {

    public UnsupportedCountryException(String countryCode) {
        super("CUSTOMER_UNSUPPORTED_COUNTRY", "Country " + countryCode + " is not supported");
    }
}
