package com.msa.customer.referencedata.controller;

import com.msa.customer.referencedata.config.SecurityConfig;
import com.msa.customer.referencedata.dto.CountryResponse;
import com.msa.customer.referencedata.exception.CountryNotFoundException;
import com.msa.customer.referencedata.service.CountryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryController.class)
@Import(SecurityConfig.class)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CountryService countryService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void returnsCountryByCode() throws Exception {
        given(countryService.findByCode("VN")).willReturn(new CountryResponse("VN", "Viet Nam", "+84", true));

        mockMvc.perform(get("/api/v1/countries/VN").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Viet Nam"));
    }

    @Test
    void unknownCountryReturns404() throws Exception {
        given(countryService.findByCode("XX")).willThrow(new CountryNotFoundException("XX"));

        mockMvc.perform(get("/api/v1/countries/XX").with(jwt())).andExpect(status().isNotFound());
    }
}
