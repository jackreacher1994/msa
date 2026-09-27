package com.msa.customer.core.infra.adapter.outbound.aop.observability;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

/**
 * Observability as a cross-cutting concern (CDI interceptor, replaces Spring AOP):
 * every observed use case gets its own span and increments a business metric.
 * Only the vendor-neutral OpenTelemetry API is used; the Quarkus OTel extension supplies the SDK.
 */
@ObservedUseCase
@Interceptor
@Priority(Interceptor.Priority.PLATFORM_BEFORE + 100)
public class UseCaseObservabilityInterceptor {

    private static final String INSTRUMENTATION_SCOPE = "com.msa.customer.core";
    private static final AttributeKey<String> USE_CASE = AttributeKey.stringKey("use_case");
    private static final AttributeKey<String> OUTCOME = AttributeKey.stringKey("outcome");

    private final Tracer tracer = GlobalOpenTelemetry.getTracer(INSTRUMENTATION_SCOPE);
    private final LongCounter useCaseCounter = GlobalOpenTelemetry.getMeter(INSTRUMENTATION_SCOPE)
            .counterBuilder("customer.use_case.invocations")
            .setDescription("Number of Customer Core use case invocations by use case and outcome")
            .build();

    @AroundInvoke
    public Object observeUseCase(InvocationContext ctx) throws Exception {
        String useCase = ctx.getMethod().getName();
        Span span = tracer.spanBuilder("UseCase " + useCase).startSpan();
        try (Scope ignored = span.makeCurrent()) {
            Object result = ctx.proceed();
            useCaseCounter.add(1, Attributes.of(USE_CASE, useCase, OUTCOME, "success"));
            return result;
        } catch (Exception e) {
            useCaseCounter.add(1, Attributes.of(USE_CASE, useCase, OUTCOME, e.getClass().getSimpleName()));
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
