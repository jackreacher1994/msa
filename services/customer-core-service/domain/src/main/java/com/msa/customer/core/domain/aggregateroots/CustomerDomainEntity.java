package com.msa.customer.core.domain.aggregateroots;

import com.msa.customer.core.domain.commands.RegisterCustomerCommand;
import com.msa.customer.core.domain.commands.UpdateCustomerProfileCommand;
import com.msa.customer.core.domain.events.CustomerProfileUpdatedEvent;
import com.msa.customer.core.domain.events.CustomerRegisteredEvent;
import com.msa.customer.core.domain.exceptions.business.CustomerNotActiveException;
import com.msa.customer.core.domain.valueobjects.CountryCode;
import com.msa.customer.core.domain.valueobjects.CustomerId;
import com.msa.customer.core.domain.valueobjects.CustomerStatus;
import com.msa.customer.core.domain.valueobjects.EmailAddress;
import com.msa.customer.core.domain.valueobjects.FullName;
import com.msa.customer.core.domain.valueobjects.PhoneNumber;

import java.time.Instant;

/**
 * Customer aggregate root ("CustomerDomainEntity" in the skeleton's class diagram).
 * Domain data and domain business logic live here; the aggregate acts as the command handler for
 * {@link RegisterCustomerCommand} and {@link UpdateCustomerProfileCommand}, enforcing invariants
 * through value objects and emitting domain events for every state change.
 */
public class CustomerDomainEntity extends AggregateRoot<CustomerId> {

    private final EmailAddress email;
    private FullName fullName;
    private PhoneNumber phoneNumber;
    private CountryCode countryCode;
    private CustomerStatus status;
    private final Instant registeredAt;
    private Instant updatedAt;
    private final Long version;

    private CustomerDomainEntity(CustomerId id, EmailAddress email, FullName fullName, PhoneNumber phoneNumber,
                                 CountryCode countryCode, CustomerStatus status, Instant registeredAt,
                                 Instant updatedAt, Long version) {
        super(id);
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
        this.status = status;
        this.registeredAt = registeredAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

    /** Command handler: business flow "Register customer". */
    public static CustomerDomainEntity register(RegisterCustomerCommand command) {
        Instant now = Instant.now();
        CustomerDomainEntity customer = new CustomerDomainEntity(
                CustomerId.newId(),
                new EmailAddress(command.email()),
                new FullName(command.fullName()),
                new PhoneNumber(command.phoneNumber()),
                new CountryCode(command.countryCode()),
                CustomerStatus.ACTIVE,
                now,
                now,
                null);
        customer.registerEvent(CustomerRegisteredEvent.of(customer));
        return customer;
    }

    /** Rebuilds an aggregate from persisted state. No events are raised: nothing happened in the domain. */
    public static CustomerDomainEntity rehydrate(CustomerId id, EmailAddress email, FullName fullName,
                                                 PhoneNumber phoneNumber, CountryCode countryCode,
                                                 CustomerStatus status, Instant registeredAt,
                                                 Instant updatedAt, Long version) {
        return new CustomerDomainEntity(id, email, fullName, phoneNumber, countryCode, status,
                registeredAt, updatedAt, version);
    }

    /**
     * Command handler: business flow "Update customer profile".
     * Business rules: only ACTIVE customers can be updated; the e-mail (login identity) is immutable.
     */
    public void updateProfile(UpdateCustomerProfileCommand command) {
        if (status != CustomerStatus.ACTIVE) {
            throw new CustomerNotActiveException(getId().value());
        }
        this.fullName = new FullName(command.fullName());
        this.phoneNumber = new PhoneNumber(command.phoneNumber());
        this.countryCode = new CountryCode(command.countryCode());
        this.updatedAt = Instant.now();
        registerEvent(CustomerProfileUpdatedEvent.of(this));
    }

    public EmailAddress getEmail() {
        return email;
    }

    public FullName getFullName() {
        return fullName;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /** Optimistic-locking version; {@code null} for a not-yet-persisted aggregate. */
    public Long getVersion() {
        return version;
    }
}
