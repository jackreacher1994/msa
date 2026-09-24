package com.msa.customer.referencedata.exception;

public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException(String code) {
        super("Country " + code + " not found");
    }
}
