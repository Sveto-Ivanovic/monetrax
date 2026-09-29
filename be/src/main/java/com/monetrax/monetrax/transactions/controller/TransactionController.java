package com.monetrax.monetrax.transactions.controller;

import com.monetrax.monetrax.auth.security.CustomUserDetails;
import com.monetrax.monetrax.transactions.dto.*;
import com.monetrax.monetrax.transactions.service.impl.TransactionAdditionalInfoServiceImpl;
import com.monetrax.monetrax.transactions.service.impl.TransactionLineItemsServiceImpl;
import com.monetrax.monetrax.transactions.service.impl.TransactionRecurrenceRuleServiceImpl;
import com.monetrax.monetrax.transactions.service.impl.TransactionServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionServiceImpl transactionService;
    private final TransactionAdditionalInfoServiceImpl transactionAdditionalInfoService;
    private final TransactionLineItemsServiceImpl transactionLineItemsService;
    private final TransactionRecurrenceRuleServiceImpl transactionRecurrenceRuleService;

    public TransactionController(TransactionServiceImpl transactionService, TransactionAdditionalInfoServiceImpl transactionAdditionalInfoService, TransactionLineItemsServiceImpl transactionLineItemsService, TransactionRecurrenceRuleServiceImpl transactionRecurrenceRuleService) {
        this.transactionService = transactionService;
        this.transactionAdditionalInfoService = transactionAdditionalInfoService;
        this.transactionLineItemsService = transactionLineItemsService;
        this.transactionRecurrenceRuleService = transactionRecurrenceRuleService;
    }

    @PostMapping("/account/{account_id}/transaction/create")
    public ResponseEntity<TransactionCreateUpdateResponse> createTransaction(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id, @Valid @RequestBody TransactionCreate transactionCreate) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: POST /transactions/account/{}/transaction/create [userId={}]", account_id, userId);
        try {
            TransactionCreateUpdateResponse response = transactionService.createTransaction(transactionCreate, userId, account_id);
            log.info("POST /transactions/account/{}/transaction/create succeeded [userId={}]", account_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("POST /transactions/account/{}/transaction/create failed [userId={}]", account_id, userId, e);
            throw e;
        }
    }

    @GetMapping("/transaction/{transaction_id}/fetch")
    public ResponseEntity<TransactionInformation> getTransactionInformation(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /transactions/transaction/{}/fetch [userId={}]", transaction_id, userId);
        try {
            TransactionInformation response = transactionService.getTransactionInformation(transaction_id, userId);
            log.debug("GET /transactions/transaction/{}/fetch succeeded [userId={}]", transaction_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("GET /transactions/transaction/{}/fetch failed [userId={}]", transaction_id, userId, e);
            throw e;
        }
    }

    @GetMapping("/account/{account_id}/fetch")
    public ResponseEntity<ListOfAccountTransactions> getAccountTransactions(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /transactions/account/{}/fetch [userId={}]", account_id, userId);
        try {
            ListOfAccountTransactions response = transactionService.getAccountTransactions(account_id, userId);
            log.debug("GET /transactions/account/{}/fetch succeeded [userId={}]", account_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("GET /transactions/account/{}/fetch failed [userId={}]", account_id, userId, e);
            throw e;
        }
    }

    @PutMapping("/transaction/{transaction_id}/update")
    public ResponseEntity<TransactionCreateUpdateResponse> updateTransaction(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @Valid @RequestBody TransactionUpdate transactionUpdate) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: PUT /transactions/transaction/{}/update [userId={}]", transaction_id, userId);
        try {
            TransactionCreateUpdateResponse response = transactionService.updateTransaction(transactionUpdate, userId, transaction_id);
            log.info("PUT /transactions/transaction/{}/update succeeded [userId={}]", transaction_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("PUT /transactions/transaction/{}/update failed [userId={}]", transaction_id, userId, e);
            throw e;
        }
    }

    @DeleteMapping("/transaction/{transaction_id}/delete")
    public ResponseEntity<TransactionCreateUpdateResponse> deleteTransaction(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: DELETE /transactions/transaction/{}/delete [userId={}]", transaction_id, userId);
        try {
            TransactionCreateUpdateResponse response = transactionService.deleteTransaction(userId, transaction_id);
            log.info("DELETE /transactions/transaction/{}/delete succeeded [userId={}]", transaction_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("DELETE /transactions/transaction/{}/delete failed [userId={}]", transaction_id, userId, e);
            throw e;
        }
    }

    @PostMapping("/transaction/{transaction_id}/transaction-additional-info/create")
    public ResponseEntity<TransactionCreateUpdateResponse> createTransactionAdditionalInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @Valid @RequestBody TransactionAdditionalInfoCreate transactionAdditionalInfoCreate) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: POST /transactions/transaction/{}/transaction-additional-info/create [userId={}]", transaction_id, userId);
        try {
            TransactionCreateUpdateResponse response = transactionAdditionalInfoService.createTransactionAdditionalInfoItem(transactionAdditionalInfoCreate, userId, transaction_id);
            log.info("POST /transactions/transaction/{}/transaction-additional-info/create succeeded [userId={}]", transaction_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("POST /transactions/transaction/{}/transaction-additional-info/create failed [userId={}]", transaction_id, userId, e);
            throw e;
        }
    }

    @DeleteMapping("/transaction/{transaction_id}/transaction-additional-info/{transaction_additional_id}")
    public ResponseEntity<TransactionCreateUpdateResponse> deleteTransactionAdditionalInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @PathVariable UUID transaction_additional_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: DELETE /transactions/transaction/{}/transaction-additional-info/{} [userId={}]", transaction_id, transaction_additional_id, userId);
        try {
            TransactionCreateUpdateResponse response = transactionAdditionalInfoService.deleteTransactionAdditionalInfoItem(userId, transaction_additional_id, transaction_id);
            log.info("DELETE /transactions/transaction/{}/transaction-additional-info/{} succeeded [userId={}]", transaction_id, transaction_additional_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("DELETE /transactions/transaction/{}/transaction-additional-info/{} failed [userId={}]", transaction_id, transaction_additional_id, userId, e);
            throw e;
        }
    }

    @PostMapping("/transaction/{transaction_id}/transaction-line-product/create")
    public ResponseEntity<TransactionCreateUpdateResponse> createTransactionLineProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @Valid @RequestBody TransactionLineItemsCreate transactionLineItemsCreate) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: POST /transactions/transaction/{}/transaction-line-product/create [userId={}]", transaction_id, userId);
        try {
            TransactionCreateUpdateResponse response = transactionLineItemsService.createTransactionLineItem(transactionLineItemsCreate, userId, transaction_id);
            log.info("POST /transactions/transaction/{}/transaction-line-product/create succeeded [userId={}]", transaction_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("POST /transactions/transaction/{}/transaction-line-product/create failed [userId={}]", transaction_id, userId, e);
            throw e;
        }
    }

    @DeleteMapping("/transaction/{transaction_id}/transaction-line-product/{transaction_line_id}")
    public ResponseEntity<TransactionCreateUpdateResponse> deleteTransactionLineProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @PathVariable UUID transaction_line_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: DELETE /transactions/transaction/{}/transaction-line-product/{} [userId={}]", transaction_id, transaction_line_id, userId);
        try {
            TransactionCreateUpdateResponse response = transactionLineItemsService.deleteTransactionLineItem(userId, transaction_line_id, transaction_id);
            log.info("DELETE /transactions/transaction/{}/transaction-line-product/{} succeeded [userId={}]", transaction_id, transaction_line_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("DELETE /transactions/transaction/{}/transaction-line-product/{} failed [userId={}]", transaction_id, transaction_line_id, userId, e);
            throw e;
        }
    }

    @GetMapping("/account/{account_id}/transaction-recurrence-rule/fetch")
    public ResponseEntity<TransactionRecurrenceResponse> getAccountTransactionRules(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /transactions/account/{}/transaction-recurrence-rule/fetch [userId={}]", account_id, userId);
        try {
            TransactionRecurrenceResponse response = transactionRecurrenceRuleService.getAllTransactionRulesForAccount(account_id, userId);
            log.debug("GET /transactions/account/{}/transaction-recurrence-rule/fetch succeeded [userId={}]", account_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("GET /transactions/account/{}/transaction-recurrence-rule/fetch failed [userId={}]", account_id, userId, e);
            throw e;
        }
    }

    @DeleteMapping("/transaction/{transaction_id}/transaction-recurrence-rule/{transaction_rule_id}")
    public ResponseEntity<TransactionCreateUpdateResponse> deleteTransactionRecurrenceRule(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @PathVariable UUID transaction_rule_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: DELETE /transactions/transaction/{}/transaction-recurrence-rule/{} [userId={}]", transaction_id, transaction_rule_id, userId);
        try {
            TransactionCreateUpdateResponse response = transactionRecurrenceRuleService.deleteTransactionRule(transaction_rule_id, transaction_id, userId);
            log.info("DELETE /transactions/transaction/{}/transaction-recurrence-rule/{} succeeded [userId={}]", transaction_id, transaction_rule_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("DELETE /transactions/transaction/{}/transaction-recurrence-rule/{} failed [userId={}]", transaction_id, transaction_rule_id, userId, e);
            throw e;
        }
    }
}