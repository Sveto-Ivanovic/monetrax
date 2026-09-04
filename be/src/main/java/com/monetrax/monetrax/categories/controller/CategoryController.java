package com.monetrax.monetrax.categories.controller;

import com.monetrax.monetrax.categories.dto.*;
import com.monetrax.monetrax.categories.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/user/{user_id}/category/{category_id}")
    public ResponseEntity<CategoryInformation> fetchUserCategory(@PathVariable UUID user_id, @PathVariable UUID category_id){
        return ResponseEntity.ok().body(categoryService.getCategory(category_id, user_id));
    }

    @PostMapping("/user/{user_id}/create")
    public ResponseEntity<CategoryInformation> createUserCategory(@PathVariable UUID user_id, @Valid @RequestBody CategoryCreate categoryCreate){
        return ResponseEntity.ok().body(categoryService.createCategory(categoryCreate, user_id));
    }

    @DeleteMapping("/user/{user_id}/category/{category_id}/delete")
    public ResponseEntity<CategoryDeletionSuccess> deleteUserCategory(@PathVariable UUID user_id, @PathVariable UUID category_id){
        return ResponseEntity.ok().body(categoryService.deleteCategory(category_id, user_id));
    }

    @PatchMapping("/user/{user_id}/category/{category_id}/update")
    public ResponseEntity<CategoryInformation> updateUserCategory(@PathVariable UUID user_id, @PathVariable UUID category_id, @Valid @RequestBody CategoryUpdate categoryUpdate){
        return ResponseEntity.ok().body(categoryService.updateCategory(categoryUpdate, category_id, user_id));
    }

    @GetMapping("/user/{user_id}/all")
    public ResponseEntity<FetchAllCategoriesResponse> fetchUserCategory3(@PathVariable UUID user_id){
        return ResponseEntity.ok().body(categoryService.getAllCategories(user_id));
    }


}
