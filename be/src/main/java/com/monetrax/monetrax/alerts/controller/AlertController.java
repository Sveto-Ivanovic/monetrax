package com.monetrax.monetrax.alerts.controller;

import com.monetrax.monetrax.alerts.dto.AlertCreate;
import com.monetrax.monetrax.alerts.dto.AlertCreateUpdateDeleteResponse;
import com.monetrax.monetrax.alerts.dto.AlertInformation;
import com.monetrax.monetrax.alerts.dto.AlertUpdate;
import com.monetrax.monetrax.alerts.service.AlertService;
import com.monetrax.monetrax.auth.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping(path = "/alert/{alert_id}/fetch")
    public ResponseEntity<AlertInformation> getAlert(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID alert_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /alerts/alert/{}/fetch [userId={}]", alert_id, userId);
        try {
            AlertInformation alertInformation = alertService.getAlert(alert_id, userId);
            log.debug("GET /alerts/alert/{}/fetch succeeded [userId={}]", alert_id, userId);
            return ResponseEntity.ok().body(alertInformation);
        } catch (Exception e) {
            log.error("GET /alerts/alert/{}/fetch failed [userId={}]", alert_id, userId, e);
            throw e;
        }
    }

    @GetMapping(path = "/account/{account_id}/fetch")
    public ResponseEntity<List<AlertInformation>> getAlerts(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /alerts/account/{}/fetch [userId={}]", account_id, userId);
        try {
            List<AlertInformation> alertInformations = alertService.getAccountAlerts(account_id, userId, false);
            log.debug("GET /alerts/account/{}/fetch succeeded [userId={}, count={}]", account_id, userId, alertInformations.size());
            return ResponseEntity.ok().body(alertInformations);
        } catch (Exception e) {
            log.error("GET /alerts/account/{}/fetch failed [userId={}]", account_id, userId, e);
            throw e;
        }
    }

    @PostMapping(path = "/account/{account_id}/alert/create")
    public ResponseEntity<AlertCreateUpdateDeleteResponse> createAlert(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody AlertCreate alertCreate, @PathVariable UUID account_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: POST /alerts/account/{}/alert/create [userId={}]", account_id, userId);
        try {
            AlertCreateUpdateDeleteResponse alertCreateResponse = alertService.createAlert(alertCreate, userId, account_id);
            log.info("POST /alerts/account/{}/alert/create succeeded [userId={}]", account_id, userId);
            return ResponseEntity.ok().body(alertCreateResponse);
        } catch (Exception e) {
            log.error("POST /alerts/account/{}/alert/create failed [userId={}]", account_id, userId, e);
            throw e;
        }
    }

    @PatchMapping(path = "/alert/{alert_id}/update")
    public ResponseEntity<AlertCreateUpdateDeleteResponse> updateAlert(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody AlertUpdate alertUpdate, @PathVariable UUID alert_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: PATCH /alerts/alert/{}/update [userId={}]", alert_id, userId);
        try {
            AlertCreateUpdateDeleteResponse alertUpdateResponse = alertService.updateAlert(alertUpdate, alert_id, userId);
            log.info("PATCH /alerts/alert/{}/update succeeded [userId={}]", alert_id, userId);
            return ResponseEntity.ok().body(alertUpdateResponse);
        } catch (Exception e) {
            log.error("PATCH /alerts/alert/{}/update failed [userId={}]", alert_id, userId, e);
            throw e;
        }
    }

    @DeleteMapping(path = "/alert/{alert_id}/delete")
    public ResponseEntity<AlertCreateUpdateDeleteResponse> deleteAlert(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID alert_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: DELETE /alerts/alert/{}/delete [userId={}]", alert_id, userId);
        try {
            AlertCreateUpdateDeleteResponse alertDeleteResponse = alertService.deleteAlert(alert_id, userId);
            log.info("DELETE /alerts/alert/{}/delete succeeded [userId={}]", alert_id, userId);
            return ResponseEntity.ok().body(alertDeleteResponse);
        } catch (Exception e) {
            log.error("DELETE /alerts/alert/{}/delete failed [userId={}]", alert_id, userId, e);
            throw e;
        }
    }
}