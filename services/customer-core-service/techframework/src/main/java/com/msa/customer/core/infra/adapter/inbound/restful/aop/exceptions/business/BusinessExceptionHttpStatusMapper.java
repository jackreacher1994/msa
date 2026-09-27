package com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.business;

import com.msa.customer.core.domain.exceptions.business.BusinessException;
import com.msa.customer.core.domain.exceptions.business.CustomerNotActiveException;
import com.msa.customer.core.domain.exceptions.business.CustomerNotFoundException;
import com.msa.customer.core.domain.exceptions.business.DuplicateCustomerEmailException;
import com.msa.customer.core.domain.exceptions.business.InvalidCustomerDataException;
import com.msa.customer.core.domain.exceptions.business.UnsupportedCountryException;
import jakarta.ws.rs.core.Response;

/** Translates framework-agnostic domain exceptions into HTTP semantics (a concern of the REST adapter only). */
public final class BusinessExceptionHttpStatusMapper {

    private BusinessExceptionHttpStatusMapper() {
    }

    public static Response.Status toHttpStatus(BusinessException exception) {
        return switch (exception) {
            case CustomerNotFoundException e -> Response.Status.NOT_FOUND;
            case DuplicateCustomerEmailException e -> Response.Status.CONFLICT;
            case CustomerNotActiveException e -> Response.Status.CONFLICT;
            case UnsupportedCountryException e -> Response.Status.fromStatusCode(422);
            case InvalidCustomerDataException e -> Response.Status.BAD_REQUEST;
            default -> Response.Status.BAD_REQUEST;
        };
    }
}
