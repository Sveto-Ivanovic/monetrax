package com.monetrax.monetrax.accounts.controller;

import com.monetrax.monetrax.accounts.dto.*;
import com.monetrax.monetrax.accounts.service.AccountService;
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
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/account/{account_id}")
    public ResponseEntity<AccountInformation> fetchUserAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /accounts/account/{} [userId={}]", account_id, userId);
        try {
            AccountInformation response = accountService.getAccountInformation(userId, account_id);
            log.debug("GET /accounts/account/{} succeeded [userId={}]", account_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("GET /accounts/account/{} failed [userId={}]", account_id, userId, e);
            throw e;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<AccountListResponse> fetchUserAccounts(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(name = "includeArchive", defaultValue = "false", required = false) boolean includeArchive) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /accounts/all [userId={}, includeArchive={}]", userId, includeArchive);
        try {
            AccountListResponse response = new AccountListResponse(accountService.getAccounts(userId, includeArchive), "Retrieved accounts successfully.");
            log.debug("GET /accounts/all succeeded [userId={}]", userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("GET /accounts/all failed [userId={}, includeArchive={}]", userId, includeArchive, e);
            throw e;
        }
    }

    @GetMapping("/all/archived")
    public ResponseEntity<AccountListResponse> fetchArchivedUserAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /accounts/all/archived [userId={}]", userId);
        try {
            AccountListResponse response = new AccountListResponse(accountService.getArchivedAccounts(userId), "Retrieved archived accounts successfully.");
            log.debug("GET /accounts/all/archived succeeded [userId={}]", userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("GET /accounts/all/archived failed [userId={}]", userId, e);
            throw e;
        }
    }

    @PostMapping("/create")
    public ResponseEntity<AccountInformation> createAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody AccountCreate accountCreate) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: POST /accounts/create [userId={}]", userId);
        try {
            AccountInformation response = accountService.createAccount(accountCreate, userId);
            log.info("POST /accounts/create succeeded [userId={}]", userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("POST /accounts/create failed [userId={}]", userId, e);
            throw e;
        }
    }

    @PutMapping("/account/{account_id}/update")
    public ResponseEntity<AccountInformation> updateAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id, @Valid @RequestBody AccountUpdate accountUpdate) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: PUT /accounts/account/{}/update [userId={}]", account_id, userId);
        try {
            AccountInformation response = accountService.updateAccount(accountUpdate, account_id, userId);
            log.info("PUT /accounts/account/{}/update succeeded [userId={}]", account_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("PUT /accounts/account/{}/update failed [userId={}]", account_id, userId, e);
            throw e;
        }
    }

    @PatchMapping("/account/{account_id}/update/archive")
    public ResponseEntity<AccountInformation> updateAccountArchive(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id, @Valid @RequestBody AccountArchive accountUpdate) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: PATCH /accounts/account/{}/update/archive [userId={}, archived={}]", account_id, userId, accountUpdate.isArchived());
        try {
            AccountInformation response = accountService.archiveAccount(userId, account_id, accountUpdate.isArchived());
            log.info("PATCH /accounts/account/{}/update/archive succeeded [userId={}]", account_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("PATCH /accounts/account/{}/update/archive failed [userId={}, archived={}]", account_id, userId, accountUpdate.isArchived(), e);
            throw e;
        }
    }
}