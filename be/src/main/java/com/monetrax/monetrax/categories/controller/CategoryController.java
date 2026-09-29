package com.monetrax.monetrax.categories.controller;

import com.monetrax.monetrax.auth.security.CustomUserDetails;
import com.monetrax.monetrax.categories.dto.*;
import com.monetrax.monetrax.categories.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/{category_id}")
    public ResponseEntity<CategoryInformation> fetchUserCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID category_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /categories/category/{} [userId={}]", category_id, userId);
        try {
            CategoryInformation response = categoryService.getCategory(category_id, userId);
            log.debug("GET /categories/category/{} succeeded [userId={}]", category_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("GET /categories/category/{} failed [userId={}]", category_id, userId, e);
            throw e;
        }
    }

    @PostMapping("/create")
    public ResponseEntity<CategoryInformation> createUserCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody CategoryCreate categoryCreate) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: POST /categories/create [userId={}]", userId);
        try {
            CategoryInformation response = categoryService.createCategory(categoryCreate, userId);
            log.info("POST /categories/create succeeded [userId={}]", userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("POST /categories/create failed [userId={}]", userId, e);
            throw e;
        }
    }

    @DeleteMapping("/category/{category_id}/delete")
    public ResponseEntity<CategoryDeletionSuccess> deleteUserCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID category_id) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: DELETE /categories/category/{}/delete [userId={}]", category_id, userId);
        try {
            CategoryDeletionSuccess response = categoryService.deleteCategory(category_id, userId);
            log.info("DELETE /categories/category/{}/delete succeeded [userId={}]", category_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("DELETE /categories/category/{}/delete failed [userId={}]", category_id, userId, e);
            throw e;
        }
    }

    @PatchMapping("/category/{category_id}/update")
    public ResponseEntity<CategoryInformation> updateUserCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID category_id, @Valid @RequestBody CategoryUpdate categoryUpdate) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: PATCH /categories/category/{}/update [userId={}]", category_id, userId);
        try {
            CategoryInformation response = categoryService.updateCategory(categoryUpdate, category_id, userId);
            log.info("PATCH /categories/category/{}/update succeeded [userId={}]", category_id, userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("PATCH /categories/category/{}/update failed [userId={}]", category_id, userId, e);
            throw e;
        }
    }

    @GetMapping("/all")
    public ResponseEntity<FetchAllCategoriesResponse> fetchAllUserCategories(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UUID userId = UUID.fromString(customUserDetails.getUserId());
        log.info("Endpoint called: GET /categories/all [userId={}]", userId);
        try {
            FetchAllCategoriesResponse response = categoryService.getAllCategories(userId);
            log.debug("GET /categories/all succeeded [userId={}]", userId);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("GET /categories/all failed [userId={}]", userId, e);
            throw e;
        }
    }
}