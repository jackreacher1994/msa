package com.msa.customer.core.infra.adapter.configs;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    /** Timeouts implement the Timeout resilience pattern: never wait indefinitely on a downstream service. */
    @Bean
    RestClient referenceDataRestClient(RestClient.Builder builder, ReferenceDataProperties properties) {
        var settings = ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(properties.connectTimeout())
                .withReadTimeout(properties.readTimeout());
        return builder
                .baseUrl(properties.baseUrl())
                .requestFactory(ClientHttpRequestFactoryBuilder.jdk().build(settings))
                .build();
    }
}
