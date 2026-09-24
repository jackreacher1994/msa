package com.msa.customer.core.infra.adapter.inbound.restful.controllers;

import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos.CustomerResponseDTO;

import java.time.ZoneOffset;

/** Maps the domain model to the published API model, keeping the two free to evolve independently. */
final class CustomerRestMapper {

    private CustomerRestMapper() {
    }

    static CustomerResponseDTO toResponse(CustomerDomainEntity customer) {
        return new CustomerResponseDTO()
                .id(customer.getId().value())
                .fullName(customer.getFullName().value())
                .email(customer.getEmail().value())
                .phoneNumber(customer.getPhoneNumber().value())
                .countryCode(customer.getCountryCode().value())
                .status(CustomerResponseDTO.StatusEnum.fromValue(customer.getStatus().name()))
                .registeredAt(customer.getRegisteredAt().atOffset(ZoneOffset.UTC))
                .updatedAt(customer.getUpdatedAt().atOffset(ZoneOffset.UTC));
    }
}
