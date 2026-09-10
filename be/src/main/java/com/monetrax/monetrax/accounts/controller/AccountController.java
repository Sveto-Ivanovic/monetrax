package com.monetrax.monetrax.accounts.controller;

import com.monetrax.monetrax.accounts.dto.*;
import com.monetrax.monetrax.accounts.service.AccountService;
import com.monetrax.monetrax.auth.security.CustomUserDetails;
import com.monetrax.monetrax.categories.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/account/{account_id}")
    public ResponseEntity<AccountInformation> fetchUserAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id){
        return ResponseEntity.ok().body(accountService.getAccountInformation(UUID.fromString(customUserDetails.getUserId()), account_id));
    }

    @GetMapping("/all")
    public ResponseEntity<AccountListResponse> fetchUserAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(name = "includeArchive", defaultValue = "false", required = false) boolean includeArchive){
        return ResponseEntity.ok().body(new AccountListResponse(accountService.getAccounts(UUID.fromString(customUserDetails.getUserId()), includeArchive), "Retrieved accounts successfully."));
    }

    @GetMapping("/all/archived")
    public ResponseEntity<AccountListResponse> fetchArchivedUserAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok().body(new AccountListResponse(accountService.getArchivedAccounts(UUID.fromString(customUserDetails.getUserId())), "Retrieved archived accounts successfully."));
    }

    @PostMapping("/create")
    public ResponseEntity<AccountInformation> createCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid AccountCreate accountCreate){
        return ResponseEntity.ok().body(accountService.createAccount(accountCreate, UUID.fromString(customUserDetails.getUserId())));
    }

    @PostMapping("/account/{account_id}/update")
    public ResponseEntity<AccountInformation> updateAccount(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id, @Valid @RequestBody AccountUpdate accountUpdate){
        return ResponseEntity.ok().body(accountService.updateAccount(accountUpdate, account_id, UUID.fromString(customUserDetails.getUserId())));
    }

    @PatchMapping("/account/{account_id}/update/archive")
    public ResponseEntity<AccountInformation> updateAccountArchive(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id, @Valid @RequestBody AccountArchive accountUpdate){
        return ResponseEntity.ok().body(accountService.archiveAccount( UUID.fromString(customUserDetails.getUserId()), account_id, accountUpdate.isArchived()));
    }
}
