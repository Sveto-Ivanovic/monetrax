package com.monetrax.monetrax.alerts.service;

import com.monetrax.monetrax.alerts.dto.AlertCreate;
import com.monetrax.monetrax.alerts.dto.AlertCreateUpdateDeleteResponse;
import com.monetrax.monetrax.alerts.dto.AlertInformation;
import com.monetrax.monetrax.alerts.dto.AlertUpdate;

import java.util.List;
import java.util.UUID;

public interface AlertService {
    AlertInformation getAlert(UUID alertId, UUID userId);
    List<AlertInformation> getAccountAlerts(UUID accountId, UUID userId, boolean includeOnlyActiveAlerts);
    AlertCreateUpdateDeleteResponse createAlert(AlertCreate alertCreate, UUID userId, UUID accountId);
    AlertCreateUpdateDeleteResponse deleteAlert(UUID alertId, UUID userId);
    AlertCreateUpdateDeleteResponse updateAlert(AlertUpdate alertUpdate, UUID alertId, UUID userId);
}
