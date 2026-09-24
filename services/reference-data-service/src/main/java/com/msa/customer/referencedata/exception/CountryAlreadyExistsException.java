package com.msa.customer.referencedata.exception;

public class CountryAlreadyExistsException extends RuntimeException {

    public CountryAlreadyExistsException(String code) {
        super("Country " + code + " already exists");
    }
}
