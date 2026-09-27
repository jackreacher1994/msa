package com.msa.customer.core.infra.adapter.configs;

import com.msa.customer.core.application.ports.inbound.commandservices.CustomerCommandInboundPort;
import com.msa.customer.core.application.ports.inbound.commandservices.CustomerCommandInboundPortImpl;
import com.msa.customer.core.application.ports.inbound.queryservices.CustomerQueryInboundPort;
import com.msa.customer.core.application.ports.inbound.queryservices.CustomerQueryInboundPortImpl;
import com.msa.customer.core.application.ports.outbound.repositories.eventpublisher.CustomerEventPublisherOutboundPort;
import com.msa.customer.core.application.ports.outbound.repositories.persistence.CustomerRepositoryOutboundPort;
import com.msa.customer.core.application.ports.outbound.repositories.referencedata.ReferenceDataOutboundPort;
import com.msa.customer.core.domain.services.CustomerDomainService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

/**
 * Composition root. The domain and application modules are plain Java (no CDI annotations),
 * so their objects are wired here via CDI producers and the ports are bound to adapters.
 */
@Singleton
public class BeanConfig {

    @Produces
    @ApplicationScoped
    CustomerDomainService customerDomainService() {
        return new CustomerDomainService();
    }

    @Produces
    @ApplicationScoped
    CustomerCommandInboundPort customerCommandInboundPort(
            CustomerRepositoryOutboundPort customerRepositoryOutboundPort,
            CustomerEventPublisherOutboundPort customerEventPublisherOutboundPort,
            ReferenceDataOutboundPort referenceDataOutboundPort,
            CustomerDomainService customerDomainService) {
        return new CustomerCommandInboundPortImpl(customerRepositoryOutboundPort,
                customerEventPublisherOutboundPort, referenceDataOutboundPort, customerDomainService);
    }

    @Produces
    @ApplicationScoped
    CustomerQueryInboundPort customerQueryInboundPort(
            CustomerRepositoryOutboundPort customerRepositoryOutboundPort) {
        return new CustomerQueryInboundPortImpl(customerRepositoryOutboundPort);
    }
}
