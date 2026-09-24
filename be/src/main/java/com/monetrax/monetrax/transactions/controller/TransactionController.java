package com.monetrax.monetrax.transactions.controller;

import com.monetrax.monetrax.auth.security.CustomUserDetails;
import com.monetrax.monetrax.transactions.dto.*;
import com.monetrax.monetrax.transactions.entity.TransactionRecurrenceRuleEntity;
import com.monetrax.monetrax.transactions.service.impl.TransactionAdditionalInfoServiceImpl;
import com.monetrax.monetrax.transactions.service.impl.TransactionLineItemsServiceImpl;
import com.monetrax.monetrax.transactions.service.impl.TransactionRecurrenceRuleServiceImpl;
import com.monetrax.monetrax.transactions.service.impl.TransactionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionServiceImpl transactionService;
    private final TransactionAdditionalInfoServiceImpl transactionAdditionalInfoService;
    private  final TransactionLineItemsServiceImpl transactionLineItemsService;
    private  final TransactionRecurrenceRuleServiceImpl transactionRecurrenceRuleService;

    public TransactionController(TransactionServiceImpl transactionService, TransactionAdditionalInfoServiceImpl transactionAdditionalInfoService, TransactionLineItemsServiceImpl transactionLineItemsService, TransactionRecurrenceRuleServiceImpl transactionRecurrenceRuleService) {
        this.transactionService = transactionService;
        this.transactionAdditionalInfoService = transactionAdditionalInfoService;
        this.transactionLineItemsService = transactionLineItemsService;
        this.transactionRecurrenceRuleService = transactionRecurrenceRuleService;
    }

    @PostMapping("/account/{account_id}/transaction/create")
    public ResponseEntity<TransactionCreateUpdateResponse> createTransaction(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id, @Valid @RequestBody TransactionCreate transactionCreate){
        return ResponseEntity.ok().body(transactionService.createTransaction(transactionCreate, UUID.fromString(customUserDetails.getUserId()), account_id));
    }

    @GetMapping("/transaction/{transaction_id}/fetch")
    public ResponseEntity<TransactionInformation> getTransactionInformation(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id){
        return ResponseEntity.ok().body(transactionService.getTransactionInformation(transaction_id, UUID.fromString(customUserDetails.getUserId())));
    }

    @GetMapping("/account/{account_id}/fetch")
    public ResponseEntity<ListOfAccountTransactions> getAccountTransactions(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id){
        return ResponseEntity.ok().body(transactionService.getAccountTransactions(account_id, UUID.fromString(customUserDetails.getUserId())));
    }

    @PutMapping("/transaction/{transaction_id}/update")
    public ResponseEntity<TransactionCreateUpdateResponse> createTransaction(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @Valid @RequestBody TransactionUpdate transactionUpdate){
        return ResponseEntity.ok().body(transactionService.updateTransaction(transactionUpdate, UUID.fromString(customUserDetails.getUserId()), transaction_id));
    }

    @DeleteMapping("/transaction/{transaction_id}/delete")
    public ResponseEntity<TransactionCreateUpdateResponse> deleteTransaction(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id){
        return ResponseEntity.ok().body(transactionService.deleteTransaction(UUID.fromString(customUserDetails.getUserId()), transaction_id));
    }

    @PostMapping("/transaction/{transaction_id}/transaction-additional-info/create")
    public ResponseEntity<TransactionCreateUpdateResponse> createTransactionAdditionalInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @Valid @RequestBody TransactionAdditionalInfoCreate transactionAdditionalInfoCreate){
        return ResponseEntity.ok().body(transactionAdditionalInfoService.createTransactionAdditionalInfoItem(transactionAdditionalInfoCreate, UUID.fromString(customUserDetails.getUserId()), transaction_id));
    }

    @DeleteMapping("/transaction/{transaction_id}/transaction-additional-info/{transaction_additional_id}")
    public ResponseEntity<TransactionCreateUpdateResponse> deleteTransactionAdditionalInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @PathVariable UUID transaction_additional_id){
        return ResponseEntity.ok().body(transactionAdditionalInfoService.deleteTransactionAdditionalInfoItem( UUID.fromString(customUserDetails.getUserId()), transaction_additional_id, transaction_id));
    }

    @PostMapping("/transaction/{transaction_id}/transaction-line-product/create")
    public ResponseEntity<TransactionCreateUpdateResponse> createTransactionLineProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @Valid @RequestBody TransactionLineItemsCreate transactionLineItemsCreate){
        return ResponseEntity.ok().body(transactionLineItemsService.createTransactionLineItem(transactionLineItemsCreate, UUID.fromString(customUserDetails.getUserId()), transaction_id));
    }

    @DeleteMapping("/transaction/{transaction_id}/transaction-line-product/{transaction_line_id}")
    public ResponseEntity<TransactionCreateUpdateResponse> deleteTransactionLineProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @PathVariable UUID transaction_line_id){
        return ResponseEntity.ok().body(transactionLineItemsService.deleteTransactionLineItem( UUID.fromString(customUserDetails.getUserId()), transaction_line_id, transaction_id));
    }


    @GetMapping("/account/{account_id}/transaction-recurrence-rule/fetch")
    public ResponseEntity<TransactionRecurrenceResponse> getAccountTransactionRules(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID account_id){
        return ResponseEntity.ok().body(transactionRecurrenceRuleService.getAllTransactionRulesForAccount(account_id, UUID.fromString(customUserDetails.getUserId())));
    }

    @DeleteMapping("/transaction/{transaction_id}/transaction-recurrence-rule/{transaction_rule_id}")
    public ResponseEntity<TransactionCreateUpdateResponse> deleteTransactionRecurrenceRule(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID transaction_id, @PathVariable UUID transaction_rule_id){
        return ResponseEntity.ok().body(transactionRecurrenceRuleService.deleteTransactionRule(transaction_rule_id, transaction_id, UUID.fromString(customUserDetails.getUserId())));
    }
}
