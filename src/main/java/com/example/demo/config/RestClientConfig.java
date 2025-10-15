package com.example.demo.config;

import com.example.demo.interceptor.RestClientInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${speech.service.url.http}")
    private String speechServiceUrl;

    @Bean
    public RestClient speechServiceRestClient() {
        return RestClient.builder()
                .baseUrl(speechServiceUrl)
                .requestInterceptor(new RestClientInterceptor()) // <-- REGISTER THE INTERCEPTOR HERE
                .build();
    }
}
