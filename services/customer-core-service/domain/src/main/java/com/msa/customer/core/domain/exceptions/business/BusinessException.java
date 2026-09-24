package com.msa.customer.core.domain.exceptions.business;

/** Base type for violations of business rules. Carries a stable error code for API clients. */
public abstract class BusinessException extends RuntimeException {

    private final String errorCode;

    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
