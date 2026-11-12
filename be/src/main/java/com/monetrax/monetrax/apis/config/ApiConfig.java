package com.monetrax.monetrax.apis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class ApiConfig {



    @Bean
    RestClient getFrankfurterClient( RestClient.Builder builder, @Value("${external-apis.frankfurter.baseUrl}") String baseUrl,
                                     @Value("${external-apis.frankfurter.readTimeout}") int readTimeout,
                                     @Value("${external-apis.frankfurter.connectTimeout}") int connectTimeout
                                     ){

        var factory =new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(connectTimeout))
                .build());

        factory.setReadTimeout(Duration.ofSeconds(readTimeout));


        return builder.clone().baseUrl(baseUrl).requestFactory(factory).build();
    }










}
