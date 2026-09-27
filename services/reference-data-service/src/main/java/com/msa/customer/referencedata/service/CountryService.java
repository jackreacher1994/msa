package com.msa.customer.referencedata.service;

import com.msa.customer.referencedata.dto.CountryRequest;
import com.msa.customer.referencedata.dto.CountryResponse;
import com.msa.customer.referencedata.entity.Country;
import com.msa.customer.referencedata.exception.CountryAlreadyExistsException;
import com.msa.customer.referencedata.exception.CountryNotFoundException;
import com.msa.customer.referencedata.repository.CountryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Locale;

/** Service layer: transaction boundary and simple CRUD rules. */
@ApplicationScoped
public class CountryService {

    private final CountryRepository countryRepository;

    @Inject
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<CountryResponse> findAll() {
        return countryRepository.findAllByOrderByCodeAsc().stream().map(CountryResponse::from).toList();
    }

    public CountryResponse findByCode(String code) {
        return CountryResponse.from(load(code));
    }

    @Transactional
    public CountryResponse create(CountryRequest request) {
        String code = normalize(request.code());
        if (countryRepository.existsByCode(code)) {
            throw new CountryAlreadyExistsException(code);
        }
        Country country = new Country(code, request.name(), request.dialCode(), request.active());
        countryRepository.persist(country);
        return CountryResponse.from(country);
    }

    @Transactional
    public CountryResponse update(String code, CountryRequest request) {
        Country country = load(code);
        country.setName(request.name());
        country.setDialCode(request.dialCode());
        country.setActive(request.active());
        return CountryResponse.from(country);
    }

    @Transactional
    public void delete(String code) {
        countryRepository.delete(load(code));
    }

    private Country load(String code) {
        String normalized = normalize(code);
        return countryRepository.findByCode(normalized)
                .orElseThrow(() -> new CountryNotFoundException(normalized));
    }

    private static String normalize(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}
