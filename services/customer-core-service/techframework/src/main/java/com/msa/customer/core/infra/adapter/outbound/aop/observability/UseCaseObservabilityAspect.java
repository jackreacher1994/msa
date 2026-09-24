package com.msa.customer.core.infra.adapter.outbound.aop.observability;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Observability as a cross-cutting concern (AOP): every use case executed through an inbound port gets
 * its own span and increments a business metric. Only the vendor-neutral OpenTelemetry API is used; the
 * OpenTelemetry Java agent provides the SDK and ships the data over OTLP to the Collector.
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UseCaseObservabilityAspect {

    private static final String INSTRUMENTATION_SCOPE = "com.msa.customer.core";
    private static final AttributeKey<String> USE_CASE = AttributeKey.stringKey("use_case");
    private static final AttributeKey<String> OUTCOME = AttributeKey.stringKey("outcome");

    private final Tracer tracer = GlobalOpenTelemetry.getTracer(INSTRUMENTATION_SCOPE);
    private final LongCounter useCaseCounter = GlobalOpenTelemetry.getMeter(INSTRUMENTATION_SCOPE)
            .counterBuilder("customer.use_case.invocations")
            .setDescription("Number of Customer Core use case invocations by use case and outcome")
            .build();

    @Around("execution(public * com.msa.customer.core.application.ports.inbound..*(..))")
    public Object observeUseCase(ProceedingJoinPoint joinPoint) throws Throwable {
        String useCase = joinPoint.getSignature().getName();
        Span span = tracer.spanBuilder("UseCase " + useCase).startSpan();
        try (Scope ignored = span.makeCurrent()) {
            Object result = joinPoint.proceed();
            useCaseCounter.add(1, Attributes.of(USE_CASE, useCase, OUTCOME, "success"));
            return result;
        } catch (Throwable t) {
            useCaseCounter.add(1, Attributes.of(USE_CASE, useCase, OUTCOME, t.getClass().getSimpleName()));
            span.recordException(t);
            span.setStatus(StatusCode.ERROR, t.getMessage());
            throw t;
        } finally {
            span.end();
        }
    }
}
