package com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions;

import com.msa.customer.core.domain.exceptions.business.BusinessException;
import com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.business.BusinessExceptionHttpStatusMapper;
import com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.description.ErrorDescription;
import com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.technicality.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Global exception handler: one place that turns exceptions into the uniform {@link ErrorDescription} body. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ErrorDescription> handleBusiness(BusinessException e) {
        return ResponseEntity.status(BusinessExceptionHttpStatusMapper.toHttpStatus(e))
                .body(ErrorDescription.of(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(TechnicalException.class)
    ResponseEntity<ErrorDescription> handleTechnical(TechnicalException e) {
        log.error("Technical failure: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorDescription.of(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorDescription> handleBadRequest(Exception e) {
        return ResponseEntity.badRequest().body(ErrorDescription.of("REQUEST_INVALID", "Malformed or invalid request"));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorDescription> handleUnexpected(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.internalServerError()
                .body(ErrorDescription.of("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
