package com.msa.customer.referencedata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/** CRUD variant: the JPA entity is the model; there is no separate domain layer. */
@Entity
@Table(name = "countries")
public class Country {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 2)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "dial_code", nullable = false)
    private String dialCode;

    @Column(nullable = false)
    private boolean active;

    protected Country() {
    }

    public Country(String code, String name, String dialCode, boolean active) {
        this.id = UUID.randomUUID();
        this.code = code;
        this.name = name;
        this.dialCode = dialCode;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
