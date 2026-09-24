package com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.business;

import com.msa.customer.core.domain.exceptions.business.BusinessException;
import com.msa.customer.core.domain.exceptions.business.CustomerNotActiveException;
import com.msa.customer.core.domain.exceptions.business.CustomerNotFoundException;
import com.msa.customer.core.domain.exceptions.business.DuplicateCustomerEmailException;
import com.msa.customer.core.domain.exceptions.business.InvalidCustomerDataException;
import com.msa.customer.core.domain.exceptions.business.UnsupportedCountryException;
import org.springframework.http.HttpStatus;

/** Translates framework-agnostic domain exceptions into HTTP semantics (a concern of the REST adapter only). */
public final class BusinessExceptionHttpStatusMapper {

    private BusinessExceptionHttpStatusMapper() {
    }

    public static HttpStatus toHttpStatus(BusinessException exception) {
        return switch (exception) {
            case CustomerNotFoundException e -> HttpStatus.NOT_FOUND;
            case DuplicateCustomerEmailException e -> HttpStatus.CONFLICT;
            case CustomerNotActiveException e -> HttpStatus.CONFLICT;
            case UnsupportedCountryException e -> HttpStatus.UNPROCESSABLE_ENTITY;
            case InvalidCustomerDataException e -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
