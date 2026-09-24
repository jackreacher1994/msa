package com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.description;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/** Uniform error body returned by every endpoint (referenced as "ErrorDescription" in the OpenAPI contract). */
public record ErrorDescription(String code, String message, OffsetDateTime timestamp) {

    public static ErrorDescription of(String code, String message) {
        return new ErrorDescription(code, message, OffsetDateTime.now(ZoneOffset.UTC));
    }
}
