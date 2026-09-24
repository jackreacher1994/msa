package com.msa.customer.core.infra.adapter.configs;

import com.msa.customer.core.application.ports.inbound.commandservices.CustomerCommandInboundPort;
import com.msa.customer.core.application.ports.inbound.commandservices.CustomerCommandInboundPortImpl;
import com.msa.customer.core.application.ports.inbound.queryservices.CustomerQueryInboundPort;
import com.msa.customer.core.application.ports.inbound.queryservices.CustomerQueryInboundPortImpl;
import com.msa.customer.core.application.ports.outbound.repositories.eventpublisher.CustomerEventPublisherOutboundPort;
import com.msa.customer.core.application.ports.outbound.repositories.persistence.CustomerRepositoryOutboundPort;
import com.msa.customer.core.application.ports.outbound.repositories.referencedata.ReferenceDataOutboundPort;
import com.msa.customer.core.domain.services.CustomerDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root. The domain and application modules are plain Java (no Spring annotations), so their
 * objects are wired here and the ports are bound to the techframework adapters (dependency inversion).
 */
@Configuration
public class BeanConfig {

    @Bean
    CustomerDomainService customerDomainService() {
        return new CustomerDomainService();
    }

    @Bean
    CustomerCommandInboundPort customerCommandInboundPort(CustomerRepositoryOutboundPort customerRepositoryOutboundPort,
                                                          CustomerEventPublisherOutboundPort customerEventPublisherOutboundPort,
                                                          ReferenceDataOutboundPort referenceDataOutboundPort,
                                                          CustomerDomainService customerDomainService) {
        return new CustomerCommandInboundPortImpl(customerRepositoryOutboundPort, customerEventPublisherOutboundPort,
                referenceDataOutboundPort, customerDomainService);
    }

    @Bean
    CustomerQueryInboundPort customerQueryInboundPort(CustomerRepositoryOutboundPort customerRepositoryOutboundPort) {
        return new CustomerQueryInboundPortImpl(customerRepositoryOutboundPort);
    }
}
