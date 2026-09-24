package com.msa.customer.core.application.ports.inbound.commandservices;

import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.commands.RegisterCustomerCommand;
import com.msa.customer.core.domain.commands.UpdateCustomerProfileCommand;

/**
 * Inbound (driving) port for state-changing use cases. Inbound adapters (REST controllers, message
 * consumers) depend on this interface, never on its implementation. Queries live in a separate port (CQS).
 */
public interface CustomerCommandInboundPort {

    CustomerDomainEntity registerCustomer(RegisterCustomerCommand command);

    CustomerDomainEntity updateCustomerProfile(UpdateCustomerProfileCommand command);
}
