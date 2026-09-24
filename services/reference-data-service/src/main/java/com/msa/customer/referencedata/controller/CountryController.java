package com.msa.customer.referencedata.controller;

import com.msa.customer.referencedata.dto.CountryRequest;
import com.msa.customer.referencedata.dto.CountryResponse;
import com.msa.customer.referencedata.service.CountryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controller layer: HTTP mapping and validation only; delegates to the service layer. */
@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping
    public List<CountryResponse> findAll() {
        return countryService.findAll();
    }

    @GetMapping("/{code}")
    public CountryResponse findByCode(@PathVariable String code) {
        return countryService.findByCode(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CountryResponse create(@Valid @RequestBody CountryRequest request) {
        return countryService.create(request);
    }

    @PutMapping("/{code}")
    public CountryResponse update(@PathVariable String code, @Valid @RequestBody CountryRequest request) {
        return countryService.update(code, request);
    }

    @DeleteMapping("/{code}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String code) {
        countryService.delete(code);
    }
}
