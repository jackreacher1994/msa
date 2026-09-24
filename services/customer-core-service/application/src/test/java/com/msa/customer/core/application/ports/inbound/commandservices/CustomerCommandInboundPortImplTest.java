package com.msa.customer.core.application.ports.inbound.commandservices;

import com.msa.customer.core.application.ports.outbound.dtos.CountryDTO;
import com.msa.customer.core.application.ports.outbound.repositories.persistence.CustomerRepositoryOutboundPort;
import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.commands.RegisterCustomerCommand;
import com.msa.customer.core.domain.commands.UpdateCustomerProfileCommand;
import com.msa.customer.core.domain.events.CustomerProfileUpdatedEvent;
import com.msa.customer.core.domain.events.CustomerRegisteredEvent;
import com.msa.customer.core.domain.events.DomainEvent;
import com.msa.customer.core.domain.exceptions.business.CustomerNotFoundException;
import com.msa.customer.core.domain.exceptions.business.DuplicateCustomerEmailException;
import com.msa.customer.core.domain.exceptions.business.UnsupportedCountryException;
import com.msa.customer.core.domain.services.CustomerDomainService;
import com.msa.customer.core.domain.valueobjects.CustomerId;
import com.msa.customer.core.domain.valueobjects.EmailAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** The application layer is tested with in-memory fakes of its outbound ports: no Spring, no database. */
class CustomerCommandInboundPortImplTest {

    private final InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
    private final List<DomainEvent> publishedEvents = new ArrayList<>();
    private final Map<String, CountryDTO> countries = Map.of(
            "VN", new CountryDTO("VN", "Viet Nam", true),
            "SG", new CountryDTO("SG", "Singapore", true),
            "AQ", new CountryDTO("AQ", "Antarctica", false));

    private CustomerCommandInboundPort port;

    @BeforeEach
    void setUp() {
        port = new CustomerCommandInboundPortImpl(repository, publishedEvents::addAll,
                code -> Optional.ofNullable(countries.get(code)), new CustomerDomainService());
    }

    @Test
    void registerPersistsCustomerAndPublishesEvent() {
        CustomerDomainEntity customer = port.registerCustomer(register("alice@example.com", "VN"));

        assertThat(repository.findById(customer.getId())).isPresent();
        assertThat(publishedEvents).singleElement().isInstanceOf(CustomerRegisteredEvent.class);
    }

    @Test
    void registerRejectsDuplicateEmail() {
        port.registerCustomer(register("alice@example.com", "VN"));

        assertThatThrownBy(() -> port.registerCustomer(register("ALICE@example.com", "VN")))
                .isInstanceOf(DuplicateCustomerEmailException.class);
    }

    @Test
    void registerRejectsUnknownOrInactiveCountry() {
        assertThatThrownBy(() -> port.registerCustomer(register("a@example.com", "US")))
                .isInstanceOf(UnsupportedCountryException.class);
        assertThatThrownBy(() -> port.registerCustomer(register("b@example.com", "AQ")))
                .isInstanceOf(UnsupportedCountryException.class);
        assertThat(publishedEvents).isEmpty();
    }

    @Test
    void updateProfilePersistsChangesAndPublishesEvent() {
        CustomerDomainEntity customer = port.registerCustomer(register("alice@example.com", "VN"));
        publishedEvents.clear();

        CustomerDomainEntity updated = port.updateCustomerProfile(new UpdateCustomerProfileCommand(
                customer.getId().value(), "Alice Tran", "+6591234567", "SG"));

        assertThat(updated.getCountryCode().value()).isEqualTo("SG");
        assertThat(publishedEvents).singleElement().isInstanceOf(CustomerProfileUpdatedEvent.class);
    }

    @Test
    void updateProfileOfUnknownCustomerFails() {
        assertThatThrownBy(() -> port.updateCustomerProfile(new UpdateCustomerProfileCommand(
                UUID.randomUUID(), "X", "+84901234567", "VN")))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    private static RegisterCustomerCommand register(String email, String country) {
        return new RegisterCustomerCommand("Alice Nguyen", email, "+84901234567", country);
    }

    private static final class InMemoryCustomerRepository implements CustomerRepositoryOutboundPort {

        private final Map<CustomerId, CustomerDomainEntity> store = new LinkedHashMap<>();

        @Override
        public CustomerDomainEntity save(CustomerDomainEntity customer) {
            store.put(customer.getId(), customer);
            return customer;
        }

        @Override
        public Optional<CustomerDomainEntity> findById(CustomerId customerId) {
            return Optional.ofNullable(store.get(customerId));
        }

        @Override
        public Optional<CustomerDomainEntity> findByEmail(EmailAddress email) {
            return store.values().stream().filter(c -> c.getEmail().equals(email)).findFirst();
        }

        @Override
        public List<CustomerDomainEntity> findAll(int limit) {
            return store.values().stream().limit(limit).toList();
        }
    }
}
