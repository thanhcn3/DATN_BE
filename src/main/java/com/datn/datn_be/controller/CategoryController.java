package com.datn.datn_be.controller;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.dto.CategoryResponse;
import com.datn.datn_be.dto.CreateCategoryRequest;
import com.datn.datn_be.dto.UpdateCategoryRequest;
import com.datn.datn_be.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@RequestBody CreateCategoryRequest request) {
        try {
            CategoryResponse response = categoryService.createCategory(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(0, "Category created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Integer categoryId) {
        try {
            CategoryResponse response = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Category retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        try {
            List<CategoryResponse> response = categoryService.getAllCategories();
            return ResponseEntity.ok(new ApiResponse<>(0, "Categories retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories() {
        try {
            List<CategoryResponse> response = categoryService.getRootCategories();
            return ResponseEntity.ok(new ApiResponse<>(0, "Root categories retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getSubcategoriesByParentId(@PathVariable Integer parentId) {
        try {
            List<CategoryResponse> response = categoryService.getSubcategoriesByParentId(parentId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Subcategories retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Integer categoryId,
            @RequestBody UpdateCategoryRequest request) {
        try {
            CategoryResponse response = categoryService.updateCategory(categoryId, request);
            return ResponseEntity.ok(new ApiResponse<>(0, "Category updated successfully", response));
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Category deleted successfully", null));
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/hierarchy/all")
    public ResponseEntity<ApiResponse<List<com.datn.datn_be.dto.CategoryDetailResponse>>> getAllCategoriesWithHierarchy() {
        try {
            List<com.datn.datn_be.dto.CategoryDetailResponse> response = categoryService.getAllCategoriesWithHierarchy();
            return ResponseEntity.ok(new ApiResponse<>(0, "Categories with hierarchy retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/hierarchy/root")
    public ResponseEntity<ApiResponse<List<com.datn.datn_be.dto.CategoryDetailResponse>>> getRootCategoriesWithSubcategories() {
        try {
            List<com.datn.datn_be.dto.CategoryDetailResponse> response = categoryService.getRootCategoriesWithSubcategories();
            return ResponseEntity.ok(new ApiResponse<>(0, "Root categories with subcategories retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
