package com.msa.customer.referencedata.repository;

import com.msa.customer.referencedata.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<Country, UUID> {

    Optional<Country> findByCode(String code);

    boolean existsByCode(String code);

    List<Country> findAllByOrderByCodeAsc();
}
