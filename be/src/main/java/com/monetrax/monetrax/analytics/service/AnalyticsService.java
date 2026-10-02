package com.monetrax.monetrax.analytics.service;

import com.monetrax.monetrax.analytics.dto.AnalyticsRequest;
import com.monetrax.monetrax.analytics.dto.AnalyticsResponse;

import java.util.UUID;

public interface AnalyticsService {

    AnalyticsResponse getAnalytics(AnalyticsRequest analyticsRequest, UUID userId);
}
