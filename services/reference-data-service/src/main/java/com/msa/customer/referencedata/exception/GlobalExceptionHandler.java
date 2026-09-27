package com.msa.customer.referencedata.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

/**
 * Centralized exception handling. One mapper per failure type, returning a
 * uniform JSON body (replaces Spring's ProblemDetail-based advice).
 */
public class GlobalExceptionHandler {

    @Provider
    public static class CountryNotFoundMapper implements ExceptionMapper<CountryNotFoundException> {
        @Override
        public Response toResponse(CountryNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("title", e.getMessage(), "status", 404))
                    .build();
        }
    }

    @Provider
    public static class CountryAlreadyExistsMapper implements ExceptionMapper<CountryAlreadyExistsException> {
        @Override
        public Response toResponse(CountryAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("title", e.getMessage(), "status", 409))
                    .build();
        }
    }

    @Provider
    public static class ValidationMapper implements ExceptionMapper<ConstraintViolationException> {
        @Override
        public Response toResponse(ConstraintViolationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(Map.of("title", "Invalid request", "status", 400))
                    .build();
        }
    }
}
