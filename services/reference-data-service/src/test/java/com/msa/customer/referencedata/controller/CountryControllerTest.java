package com.msa.customer.referencedata.controller;

import com.msa.customer.referencedata.dto.CountryResponse;
import com.msa.customer.referencedata.exception.CountryNotFoundException;
import com.msa.customer.referencedata.service.CountryService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class CountryControllerTest {

    @InjectMock
    CountryService countryService;

    @Test
    @TestSecurity(user = "alice")
    void returnsCountryByCode() {
        Mockito.when(countryService.findByCode("VN"))
                .thenReturn(new CountryResponse("VN", "Viet Nam", "+84", true));

        given()
                .when().get("/api/v1/countries/VN")
                .then()
                .statusCode(200)
                .body("name", is("Viet Nam"));
    }

    @Test
    @TestSecurity(user = "alice")
    void unknownCountryReturns404() {
        Mockito.when(countryService.findByCode("XX")).thenThrow(new CountryNotFoundException("XX"));

        given()
                .when().get("/api/v1/countries/XX")
                .then()
                .statusCode(404);
    }

    @Test
    void unauthenticatedReturns401() {
        given()
                .when().get("/api/v1/countries/VN")
                .then()
                .statusCode(401);
    }
}
