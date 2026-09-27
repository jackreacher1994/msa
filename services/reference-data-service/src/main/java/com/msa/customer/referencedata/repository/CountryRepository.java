package com.msa.customer.referencedata.repository;

import com.msa.customer.referencedata.entity.Country;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CountryRepository implements PanacheRepositoryBase<Country, UUID> {

    public Optional<Country> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    public boolean existsByCode(String code) {
        return count("code", code) > 0;
    }

    public List<Country> findAllByOrderByCodeAsc() {
        return list("order by code asc");
    }
}
