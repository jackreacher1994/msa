package com.msa.customer.core.infra.adapter.inbound.restful.controllers;

import com.msa.customer.core.application.ports.inbound.commandservices.CustomerCommandInboundPort;
import com.msa.customer.core.application.ports.inbound.queryservices.CustomerQueryInboundPort;
import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.commands.RegisterCustomerCommand;
import com.msa.customer.core.domain.exceptions.business.DuplicateCustomerEmailException;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;

@QuarkusTest
class CustomerApiImplTest {

    private static final String BODY = """
            {"fullName":"Alice Nguyen","email":"alice@example.com","phoneNumber":"+84901234567","countryCode":"VN"}
            """;

    @InjectMock
    CustomerCommandInboundPort customerCommandInboundPort;

    @InjectMock
    CustomerQueryInboundPort customerQueryInboundPort;

    @Test
    @TestSecurity(user = "alice")
    void registerReturns201WithCustomer() {
        given(customerCommandInboundPort.registerCustomer(ArgumentMatchers.any()))
                .willReturn(CustomerDomainEntity.register(
                        new RegisterCustomerCommand("Alice Nguyen", "alice@example.com", "+84901234567", "VN")));

        given()
                .contentType(JSON).body(BODY)
                .when().post("/api/v1/customers")
                .then()
                .statusCode(201)
                .body("email", is("alice@example.com"))
                .body("status", is("ACTIVE"));
    }

    @Test
    @TestSecurity(user = "alice")
    void duplicateEmailIsMappedTo409() {
        given(customerCommandInboundPort.registerCustomer(ArgumentMatchers.any()))
                .willThrow(new DuplicateCustomerEmailException("alice@example.com"));

        given()
                .contentType(JSON).body(BODY)
                .when().post("/api/v1/customers")
                .then()
                .statusCode(409)
                .body("code", is("CUSTOMER_EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void requestWithoutTokenIsRejected() {
        given()
                .contentType(JSON).body(BODY)
                .when().post("/api/v1/customers")
                .then()
                .statusCode(401);
    }
}
