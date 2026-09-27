/**
 * Customer Core :: Domain — pure business logic (aggregates, value objects,
 * commands/queries, events, domain services). No framework dependencies.
 *
 * <p>Every package except internal {@code utils} is exported so the
 * application and techframework layers can use the domain model, while
 * {@code utils} stays module-internal (stronger than Maven, which cannot
 * hide packages inside an artifact).</p>
 */
module com.msa.customer.core.domain {
    exports com.msa.customer.core.domain.aggregateroots;
    exports com.msa.customer.core.domain.commands;
    exports com.msa.customer.core.domain.queries;
    exports com.msa.customer.core.domain.events;
    exports com.msa.customer.core.domain.valueobjects;
    exports com.msa.customer.core.domain.exceptions.business;
    exports com.msa.customer.core.domain.services;
    exports com.msa.customer.core.domain.constants;
    // com.msa.customer.core.domain.utils intentionally NOT exported.
}
