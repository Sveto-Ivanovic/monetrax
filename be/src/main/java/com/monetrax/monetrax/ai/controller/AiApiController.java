package com.monetrax.monetrax.ai.controller;

import com.monetrax.monetrax.ai.dto.ResponseApiKeyStatusMsg;
import com.monetrax.monetrax.ai.entity.KeyType;
import com.monetrax.monetrax.ai.dto.RequestApiKey;
import com.monetrax.monetrax.ai.service.AiAPiService;
import com.monetrax.monetrax.auth.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/ai/keys")
public class AiApiController {

    @Autowired
    private AiAPiService aiAPiService;

    @GetMapping("/status")
    public ResponseEntity<ResponseApiKeyStatusMsg> getKeyStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /ai/keys/status [userId={}]", userId);
        try {
            ResponseApiKeyStatusMsg response = aiAPiService.getKeyStatus(userId);
            log.debug("GET /ai/keys/status succeeded [userId={}]", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("GET /ai/keys/status failed [userId={}]", userId, e);
            throw e;
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseApiKeyStatusMsg> createUpdateApiKey(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody RequestApiKey req) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: PUT /ai/keys/update [userId={}, keyType={}]", userId, req.getKeyType());
        try {
            ResponseApiKeyStatusMsg response = aiAPiService.createUpdateApiKey(req.getRawKey(), req.getKeyType(), userId);
            log.info("PUT /ai/keys/update succeeded [userId={}, keyType={}]", userId, req.getKeyType());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("PUT /ai/keys/update failed [userId={}, keyType={}]", userId, req.getKeyType(), e);
            throw e;
        }
    }

    @DeleteMapping("/delete/{keyType}")
    public ResponseEntity<ResponseApiKeyStatusMsg> deleteApiKey(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable KeyType keyType) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: DELETE /ai/keys/{} [userId={}]", keyType, userId);
        try {
            ResponseApiKeyStatusMsg response = aiAPiService.deleteApiKey(keyType, userId);
            log.info("DELETE /ai/keys/{} succeeded [userId={}]", keyType, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.warn("DELETE /ai/keys/{} failed [userId={}]: {}", keyType, userId, e.getMessage());
            throw e;
        }
    }
}
