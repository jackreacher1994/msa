package com.msa.customer.core.application.ports.inbound.commandservices;

import com.msa.customer.core.application.ports.outbound.dtos.CountryDTO;
import com.msa.customer.core.application.ports.outbound.repositories.eventpublisher.CustomerEventPublisherOutboundPort;
import com.msa.customer.core.application.ports.outbound.repositories.persistence.CustomerRepositoryOutboundPort;
import com.msa.customer.core.application.ports.outbound.repositories.referencedata.ReferenceDataOutboundPort;
import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.commands.RegisterCustomerCommand;
import com.msa.customer.core.domain.commands.UpdateCustomerProfileCommand;
import com.msa.customer.core.domain.exceptions.business.CustomerNotFoundException;
import com.msa.customer.core.domain.exceptions.business.UnsupportedCountryException;
import com.msa.customer.core.domain.services.CustomerDomainService;
import com.msa.customer.core.domain.valueobjects.CountryCode;
import com.msa.customer.core.domain.valueobjects.CustomerId;
import com.msa.customer.core.domain.valueobjects.EmailAddress;

/**
 * Application service: orchestrates a use case (load -> invoke domain -> persist -> publish events)
 * but holds no business rules itself. It only talks to outbound ports, so the concrete database,
 * broker or remote service can be swapped without touching this class. Framework-free: transactions
 * and tracing are applied by the techframework module around this port.
 */
public class CustomerCommandInboundPortImpl implements CustomerCommandInboundPort {

    private final CustomerRepositoryOutboundPort customerRepositoryOutboundPort;
    private final CustomerEventPublisherOutboundPort customerEventPublisherOutboundPort;
    private final ReferenceDataOutboundPort referenceDataOutboundPort;
    private final CustomerDomainService customerDomainService;

    public CustomerCommandInboundPortImpl(CustomerRepositoryOutboundPort customerRepositoryOutboundPort,
                                          CustomerEventPublisherOutboundPort customerEventPublisherOutboundPort,
                                          ReferenceDataOutboundPort referenceDataOutboundPort,
                                          CustomerDomainService customerDomainService) {
        this.customerRepositoryOutboundPort = customerRepositoryOutboundPort;
        this.customerEventPublisherOutboundPort = customerEventPublisherOutboundPort;
        this.referenceDataOutboundPort = referenceDataOutboundPort;
        this.customerDomainService = customerDomainService;
    }

    @Override
    public CustomerDomainEntity registerCustomer(RegisterCustomerCommand command) {
        EmailAddress email = new EmailAddress(command.email());
        customerDomainService.assertEmailIsUnique(email, customerRepositoryOutboundPort.findByEmail(email));
        assertCountryIsSupported(new CountryCode(command.countryCode()));

        CustomerDomainEntity customer = CustomerDomainEntity.register(command);
        CustomerDomainEntity saved = customerRepositoryOutboundPort.save(customer);
        customerEventPublisherOutboundPort.publish(customer.pullDomainEvents());
        return saved;
    }

    @Override
    public CustomerDomainEntity updateCustomerProfile(UpdateCustomerProfileCommand command) {
        CustomerDomainEntity customer = customerRepositoryOutboundPort.findById(new CustomerId(command.customerId()))
                .orElseThrow(() -> new CustomerNotFoundException(command.customerId()));
        assertCountryIsSupported(new CountryCode(command.countryCode()));

        customer.updateProfile(command);
        CustomerDomainEntity saved = customerRepositoryOutboundPort.save(customer);
        customerEventPublisherOutboundPort.publish(customer.pullDomainEvents());
        return saved;
    }

    /** Synchronous call to the Reference Data bounded context through an outbound port. */
    private void assertCountryIsSupported(CountryCode countryCode) {
        referenceDataOutboundPort.findCountry(countryCode.value())
                .filter(CountryDTO::active)
                .orElseThrow(() -> new UnsupportedCountryException(countryCode.value()));
    }
}
