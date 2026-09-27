package com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.technicality;

/** The event broker could not accept a domain event; surfaced as 503 so clients may retry. */
public class EventPublishException extends TechnicalException {

    public EventPublishException(Throwable cause) {
        super("EVENT_PUBLISH_FAILED", "Event broker is unavailable", cause);
    }
}
