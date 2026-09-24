package com.msa.customer.core.domain.aggregateroots;

import com.msa.customer.core.domain.commands.RegisterCustomerCommand;
import com.msa.customer.core.domain.commands.UpdateCustomerProfileCommand;
import com.msa.customer.core.domain.events.CustomerProfileUpdatedEvent;
import com.msa.customer.core.domain.events.CustomerRegisteredEvent;
import com.msa.customer.core.domain.events.DomainEvent;
import com.msa.customer.core.domain.exceptions.business.CustomerNotActiveException;
import com.msa.customer.core.domain.exceptions.business.InvalidCustomerDataException;
import com.msa.customer.core.domain.valueobjects.CustomerStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerDomainEntityTest {

    private static final RegisterCustomerCommand REGISTER =
            new RegisterCustomerCommand("Alice Nguyen", " Alice@Example.com ", "+84901234567", "vn");

    @Test
    void registerCreatesActiveCustomerWithNormalizedValuesAndRaisesEvent() {
        CustomerDomainEntity customer = CustomerDomainEntity.register(REGISTER);

        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.getEmail().value()).isEqualTo("alice@example.com");
        assertThat(customer.getCountryCode().value()).isEqualTo("VN");
        assertThat(customer.pullDomainEvents()).singleElement().isInstanceOf(CustomerRegisteredEvent.class);
        assertThat(customer.pullDomainEvents()).as("events are pulled only once").isEmpty();
    }

    @Test
    void registerRejectsInvalidData() {
        assertThatThrownBy(() -> CustomerDomainEntity.register(
                new RegisterCustomerCommand("Alice", "not-an-email", "+84901234567", "VN")))
                .isInstanceOf(InvalidCustomerDataException.class);
        assertThatThrownBy(() -> CustomerDomainEntity.register(
                new RegisterCustomerCommand("Alice", "a@b.com", "0901", "VN")))
                .isInstanceOf(InvalidCustomerDataException.class);
    }

    @Test
    void updateProfileChangesMutableDataAndKeepsEmail() {
        CustomerDomainEntity customer = CustomerDomainEntity.register(REGISTER);
        customer.pullDomainEvents();

        customer.updateProfile(new UpdateCustomerProfileCommand(customer.getId().value(),
                "Alice Tran", "+6591234567", "SG"));

        assertThat(customer.getFullName().value()).isEqualTo("Alice Tran");
        assertThat(customer.getCountryCode().value()).isEqualTo("SG");
        assertThat(customer.getEmail().value()).isEqualTo("alice@example.com");
        List<DomainEvent> events = customer.pullDomainEvents();
        assertThat(events).singleElement().isInstanceOf(CustomerProfileUpdatedEvent.class);
    }

    @Test
    void suspendedCustomerCannotBeUpdated() {
        CustomerDomainEntity active = CustomerDomainEntity.register(REGISTER);
        CustomerDomainEntity suspended = CustomerDomainEntity.rehydrate(active.getId(), active.getEmail(),
                active.getFullName(), active.getPhoneNumber(), active.getCountryCode(), CustomerStatus.SUSPENDED,
                active.getRegisteredAt(), active.getUpdatedAt(), 0L);

        assertThatThrownBy(() -> suspended.updateProfile(new UpdateCustomerProfileCommand(
                suspended.getId().value(), "Bob", "+84901234567", "VN")))
                .isInstanceOf(CustomerNotActiveException.class);
    }
}
