package com.monetrax.monetrax.alerts.service.impl;

import com.monetrax.monetrax.alerts.dto.AlertCreate;
import com.monetrax.monetrax.alerts.dto.AlertCreateUpdateDeleteResponse;
import com.monetrax.monetrax.alerts.dto.AlertInformation;
import com.monetrax.monetrax.alerts.dto.AlertUpdate;
import com.monetrax.monetrax.alerts.service.AlertService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AlertServiceImpl implements AlertService {

    @Override
    public AlertInformation getAlert(UUID alertId, UUID userId) {
        

        return null;
    }

    @Override
    public List<AlertInformation> getAccountAlerts(UUID accountId, UUID userId) {
        return List.of();
    }

    @Override
    public AlertCreateUpdateDeleteResponse createAlert(AlertCreate alertCreate, UUID userId, UUID accountId) {
        return null;
    }

    @Override
    public AlertCreateUpdateDeleteResponse deleteAlert(UUID alertId, UUID userId) {
        return null;
    }

    @Override
    public AlertCreateUpdateDeleteResponse updateAlert(AlertUpdate alertUpdate, UUID alertId, UUID userId) {
        return null;
    }

}
