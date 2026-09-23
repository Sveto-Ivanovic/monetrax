package com.monetrax.monetrax.alerts.controller;

import com.monetrax.monetrax.alerts.dto.AlertCreate;
import com.monetrax.monetrax.alerts.dto.AlertCreateUpdateDeleteResponse;
import com.monetrax.monetrax.alerts.dto.AlertInformation;
import com.monetrax.monetrax.alerts.dto.AlertUpdate;
import com.monetrax.monetrax.alerts.service.AlertService;
import com.monetrax.monetrax.auth.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/alerts")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @GetMapping(path = "/alert/{alert_id}/fetch")
    public ResponseEntity<AlertInformation> getAlert(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID alert_id){
        AlertInformation alertInformation = alertService.getAlert(alert_id,UUID.fromString(customUserDetails.getUserId()));
        return ResponseEntity.ok().body(alertInformation);
    }

    @GetMapping(path = "/account/{account_id}/fetch")
    public ResponseEntity<List<AlertInformation>> getAlerts(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id){
        List<AlertInformation> alertInformations = alertService.getAccountAlerts(account_id, UUID.fromString(customUserDetails.getUserId()), false);
        return ResponseEntity.ok().body(alertInformations);
    }

    @PostMapping(path = "/account/{account_id}/alert/create")
    public ResponseEntity<AlertCreateUpdateDeleteResponse> createAlert(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody AlertCreate alertCreate, @PathVariable UUID account_id){
        AlertCreateUpdateDeleteResponse alertCreateResponse = alertService.createAlert(alertCreate, UUID.fromString(customUserDetails.getUserId()), account_id);
        return ResponseEntity.ok().body(alertCreateResponse);
    }

    @PatchMapping(path = "/alert/{alert_id}/update")
    public ResponseEntity<AlertCreateUpdateDeleteResponse> updateAlert(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody AlertUpdate alertUpdate, @PathVariable UUID alert_id){
        AlertCreateUpdateDeleteResponse alertUpdateResponse = alertService.updateAlert(alertUpdate, alert_id, UUID.fromString(customUserDetails.getUserId()));
        return ResponseEntity.ok().body(alertUpdateResponse);
    }

    @DeleteMapping(path = "/alert/{alert_id}/delete")
    public ResponseEntity<AlertCreateUpdateDeleteResponse> deleteAlert(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID alert_id){
        AlertCreateUpdateDeleteResponse alertUpdateResponse = alertService.deleteAlert(alert_id, UUID.fromString(customUserDetails.getUserId()));
        return ResponseEntity.ok().body(alertUpdateResponse);
    }

}
