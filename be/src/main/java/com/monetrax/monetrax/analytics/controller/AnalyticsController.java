package com.monetrax.monetrax.analytics.controller;

import com.monetrax.monetrax.analytics.dto.AnalyticsRequest;
import com.monetrax.monetrax.analytics.dto.AnalyticsResponse;
import com.monetrax.monetrax.analytics.service.AnalyticsService;
import com.monetrax.monetrax.auth.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @PostMapping("/fetch")
    public ResponseEntity<AnalyticsResponse> fetchAnalytics(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody AnalyticsRequest analyticsRequest) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: POST /analytics/fetch [userId={}]", userId);
        try {
            AnalyticsResponse response = analyticsService.getAnalytics(analyticsRequest, userId);
            log.debug("POST /analytics/fetch succeeded [userId={}]", userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("POST /analytics/fetch failed [userId={}]", userId, e);
            throw e;
        }
    }
}
