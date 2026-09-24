package com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.technicality;

/** Base type for infrastructure failures (remote service down, timeouts...), as opposed to business rule violations. */
public class TechnicalException extends RuntimeException {

    private final String errorCode;

    public TechnicalException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
