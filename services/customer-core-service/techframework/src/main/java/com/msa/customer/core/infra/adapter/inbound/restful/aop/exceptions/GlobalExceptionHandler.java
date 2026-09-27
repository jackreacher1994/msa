package com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions;

import com.msa.customer.core.domain.exceptions.business.BusinessException;
import com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.business.BusinessExceptionHttpStatusMapper;
import com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.description.ErrorDescription;
import com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.technicality.TechnicalException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/** Global exception handling: turns exceptions into the uniform {@link ErrorDescription} body. */
public class GlobalExceptionHandler {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Provider
    public static class BusinessMapper implements ExceptionMapper<BusinessException> {
        @Override
        public Response toResponse(BusinessException e) {
            return Response.status(BusinessExceptionHttpStatusMapper.toHttpStatus(e))
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorDescription.of(e.getErrorCode(), e.getMessage()))
                    .build();
        }
    }

    @Provider
    public static class TechnicalMapper implements ExceptionMapper<TechnicalException> {
        @Override
        public Response toResponse(TechnicalException e) {
            LOG.errorf(e, "Technical failure: %s", e.getMessage());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorDescription.of(e.getErrorCode(), e.getMessage()))
                    .build();
        }
    }

    @Provider
    public static class ValidationMapper implements ExceptionMapper<ConstraintViolationException> {
        @Override
        public Response toResponse(ConstraintViolationException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorDescription.of("REQUEST_INVALID", "Malformed or invalid request"))
                    .build();
        }
    }

    @Provider
    public static class UnexpectedMapper implements ExceptionMapper<Exception> {
        @Override
        public Response toResponse(Exception e) {
            LOG.error("Unexpected error", e);
            return Response.serverError()
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ErrorDescription.of("INTERNAL_ERROR", "An unexpected error occurred"))
                    .build();
        }
    }
}
