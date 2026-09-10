package com.monetrax.monetrax.categories.controller;

import com.monetrax.monetrax.auth.security.CustomUserDetails;
import com.monetrax.monetrax.categories.dto.*;
import com.monetrax.monetrax.categories.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category/{category_id}")
    public ResponseEntity<CategoryInformation> fetchUserCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID category_id){
        return ResponseEntity.ok().body(categoryService.getCategory(category_id, UUID.fromString(customUserDetails.getUserId())));
    }

    @PostMapping("/create")
    public ResponseEntity<CategoryInformation> createUserCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody CategoryCreate categoryCreate){
        return ResponseEntity.ok().body(categoryService.createCategory(categoryCreate, UUID.fromString(customUserDetails.getUserId())));
    }

    @DeleteMapping("/category/{category_id}/delete")
    public ResponseEntity<CategoryDeletionSuccess> deleteUserCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID category_id){
        return ResponseEntity.ok().body(categoryService.deleteCategory(category_id, UUID.fromString(customUserDetails.getUserId())));
    }

    @PatchMapping("/category/{category_id}/update")
    public ResponseEntity<CategoryInformation> updateUserCategory(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable UUID category_id, @Valid @RequestBody CategoryUpdate categoryUpdate){
        return ResponseEntity.ok().body(categoryService.updateCategory(categoryUpdate, category_id, UUID.fromString(customUserDetails.getUserId())));
    }

    @GetMapping("/all")
    public ResponseEntity<FetchAllCategoriesResponse> fetchUserCategory3(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return ResponseEntity.ok().body(categoryService.getAllCategories(UUID.fromString(customUserDetails.getUserId())));
    }


}
