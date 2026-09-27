/**
 * Customer Core :: TechFramework — concrete technologies (Spring MVC/Security,
 * JPA, REST clients, OpenTelemetry API) and the runnable Spring Boot app.
 * Top of the dependency chain: nothing requires this module, so it exports
 * nothing. JPMS is enforced at compile time; the Spring Boot fat jar still
 * runs on the classpath (see root Dockerfile), so these {@code requires}
 * describe compile-time visibility, not a full {@code java -p -m} runtime.
 */
module com.msa.customer.core.techframework {
    requires com.msa.customer.core.application;
    requires transitive com.msa.customer.core.domain;

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.aop;
    requires spring.expression;
    requires spring.web;
    requires spring.webmvc;
    requires spring.tx;
    requires spring.orm;
    requires spring.jdbc;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.security.core;
    requires spring.security.config;
    requires spring.security.web;
    requires spring.security.crypto;
    requires spring.security.oauth2.resource.server;
    requires spring.security.oauth2.jose;
    requires spring.boot.actuator;
    requires spring.boot.actuator.autoconfigure;

    requires jakarta.persistence;
    requires jakarta.validation;
    requires jakarta.annotation;
    requires org.apache.tomcat.embed.core;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;

    requires org.aspectj.weaver;

    requires org.hibernate.orm.core;

    requires io.opentelemetry.api;
    requires io.opentelemetry.context;

    requires org.slf4j;

    // Reflection: Spring (component scanning, @Configuration, @Bean CGLIB),
    // JPA/Hibernate (entity enhancement, proxies), Jackson (de/serialization).
    // Unconditional opens keep this robust against Spring internal module
    // renames; compile-time hiding is still enforced via application exports.
    opens com.msa.customer.core;
    opens com.msa.customer.core.infra.adapter.configs;
    opens com.msa.customer.core.infra.adapter.inbound.restful.controllers;
    opens com.msa.customer.core.infra.adapter.inbound.restful.apis;
    opens com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos;
    opens com.msa.customer.core.infra.adapter.inbound.restful.filters;
    opens com.msa.customer.core.infra.adapter.inbound.restful.aop.security;
    opens com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions;
    opens com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.business;
    opens com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.description;
    opens com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.technicality;
    opens com.msa.customer.core.infra.adapter.outbound.aop.observability;
    opens com.msa.customer.core.infra.adapter.outbound.integration.repositories.springdatajpa;
    opens com.msa.customer.core.infra.adapter.outbound.integration.repositories.springdatajpa.entities;
    opens com.msa.customer.core.infra.adapter.outbound.integration.repositories.eventpublishers;
    opens com.msa.customer.core.infra.adapter.outbound.integration.repositories.restclients;
}
