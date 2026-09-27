/**
 * Customer Core :: Application — inbound ports (use cases) and outbound ports.
 * Depends only on the domain module. Framework-free: no Spring/JPA imports.
 *
 * <p>Port interfaces and DTOs are openly exported. Use-case implementations
 * ({@code ..impl} packages) are exported <em>only</em> to the techframework
 * module (the composition root that wires {@code BeanConfig}), so no other
 * consumer can depend on them at compile time. The {@code opens} directives
 * are unconditional on purpose: this module does not (and must not) require
 * Spring, so it cannot name Spring modules in a qualified {@code opens to};
 * runtime proxies (CGLIB/AOP) still need reflective access.</p>
 */
module com.msa.customer.core.application {
    requires transitive com.msa.customer.core.domain;

    exports com.msa.customer.core.application.ports.inbound.commandservices;
    exports com.msa.customer.core.application.ports.inbound.queryservices;
    exports com.msa.customer.core.application.ports.outbound.dtos;
    exports com.msa.customer.core.application.ports.outbound.repositories.persistence;
    exports com.msa.customer.core.application.ports.outbound.repositories.referencedata;
    exports com.msa.customer.core.application.ports.outbound.repositories.eventpublisher;

    exports com.msa.customer.core.application.ports.inbound.commandservices.impl
            to com.msa.customer.core.techframework;
    exports com.msa.customer.core.application.ports.inbound.queryservices.impl
            to com.msa.customer.core.techframework;

    opens com.msa.customer.core.application.ports.inbound.commandservices.impl;
    opens com.msa.customer.core.application.ports.inbound.queryservices.impl;
}
