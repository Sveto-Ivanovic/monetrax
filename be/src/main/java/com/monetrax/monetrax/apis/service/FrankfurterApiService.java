package com.monetrax.monetrax.apis.service;

import com.monetrax.monetrax.apis.dto.FrankfurterV2Response;
import com.monetrax.monetrax.apis.exceptions.ExternalAPIException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Service
public class FrankfurterApiService {

    private final RestClient restClient;

    public FrankfurterApiService(@Qualifier("getFrankfurterClient") RestClient restClient) {
        this.restClient = restClient;
    }
    public FrankfurterV2Response getConversionFactor(String from, String to, LocalDate localDate){
        try {
        return restClient.get()
                .uri("/v2/rate/{from}/{to}?date={localDate}", from, to, localDate)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, res) -> {
                    throw new ExternalAPIException(
                            "No rate found for %s -> %s on %s".formatted(from, to, localDate));
                })
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ExternalAPIException(
                            "Frankfurter rejected the request: " + res.getStatusCode());
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new ExternalAPIException(
                            "Frankfurter is unavailable: " + res.getStatusCode());
                })
                .body(FrankfurterV2Response.class);

        } catch (ResourceAccessException e) {
            throw new ExternalAPIException("Could not reach Frankfurter: "+ e.getMessage());
        }
    }




}
