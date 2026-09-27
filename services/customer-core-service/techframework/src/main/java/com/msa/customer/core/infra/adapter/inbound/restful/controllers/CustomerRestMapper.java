package com.msa.customer.core.infra.adapter.inbound.restful.controllers;

import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos.CustomerResponse;

import java.time.ZoneOffset;

/** Maps the domain model to the published API model, keeping the two free to evolve independently. */
final class CustomerRestMapper {

    private CustomerRestMapper() {
    }

    static CustomerResponse toResponse(CustomerDomainEntity customer) {
        return new CustomerResponse(
                customer.getId().value(),
                customer.getFullName().value(),
                customer.getEmail().value(),
                customer.getPhoneNumber().value(),
                customer.getCountryCode().value(),
                customer.getStatus().name(),
                customer.getRegisteredAt().atOffset(ZoneOffset.UTC),
                customer.getUpdatedAt().atOffset(ZoneOffset.UTC));
    }
}
