package com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.technicality;

/** The Reference Data service could not be reached in time; surfaced as 503 so clients may retry. */
public class ReferenceDataUnavailableException extends TechnicalException {

    public ReferenceDataUnavailableException(Throwable cause) {
        super("REFERENCE_DATA_UNAVAILABLE", "Reference data service is unavailable", cause);
    }
}
