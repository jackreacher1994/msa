package com.msa.customer.core.infra.adapter.inbound.restful.controllers;

import com.msa.customer.core.application.ports.inbound.commandservices.CustomerCommandInboundPort;
import com.msa.customer.core.application.ports.inbound.queryservices.CustomerQueryInboundPort;
import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.commands.RegisterCustomerCommand;
import com.msa.customer.core.domain.exceptions.business.DuplicateCustomerEmailException;
import com.msa.customer.core.infra.adapter.inbound.restful.aop.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerApiImpl.class)
@Import(SecurityConfig.class)
class CustomerApiImplTest {

    private static final String BODY = """
            {"fullName":"Alice Nguyen","email":"alice@example.com","phoneNumber":"+84901234567","countryCode":"VN"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerCommandInboundPort customerCommandInboundPort;

    @MockitoBean
    private CustomerQueryInboundPort customerQueryInboundPort;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void registerReturns201WithCustomer() throws Exception {
        given(customerCommandInboundPort.registerCustomer(any())).willReturn(CustomerDomainEntity.register(
                new RegisterCustomerCommand("Alice Nguyen", "alice@example.com", "+84901234567", "VN")));

        mockMvc.perform(post("/api/v1/customers").with(jwt()).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void duplicateEmailIsMappedTo409() throws Exception {
        given(customerCommandInboundPort.registerCustomer(any()))
                .willThrow(new DuplicateCustomerEmailException("alice@example.com"));

        mockMvc.perform(post("/api/v1/customers").with(jwt()).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CUSTOMER_EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void requestWithoutTokenIsRejected() throws Exception {
        mockMvc.perform(post("/api/v1/customers").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }
}
