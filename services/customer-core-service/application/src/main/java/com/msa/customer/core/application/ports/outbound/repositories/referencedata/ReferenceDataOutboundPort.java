package com.msa.customer.core.application.ports.outbound.repositories.referencedata;

import com.msa.customer.core.application.ports.outbound.dtos.CountryDTO;

import java.util.Optional;

/** Outbound port to the Reference Data (supporting) bounded context. */
public interface ReferenceDataOutboundPort {

    Optional<CountryDTO> findCountry(String countryCode);
}
