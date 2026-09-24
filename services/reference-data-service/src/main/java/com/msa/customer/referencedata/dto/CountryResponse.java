package com.msa.customer.referencedata.dto;

import com.msa.customer.referencedata.entity.Country;

public record CountryResponse(String code, String name, String dialCode, boolean active) {

    public static CountryResponse from(Country country) {
        return new CountryResponse(country.getCode(), country.getName(), country.getDialCode(), country.isActive());
    }
}
