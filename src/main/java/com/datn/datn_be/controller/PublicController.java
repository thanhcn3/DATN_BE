package com.datn.datn_be.controller;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.dto.CategoryDetailResponse;
import com.datn.datn_be.dto.PaginationResponse;
import com.datn.datn_be.dto.ProductResponse;
import com.datn.datn_be.dto.ReviewResponse;
import com.datn.datn_be.dto.ReviewSummaryResponse;
import com.datn.datn_be.service.CategoryService;
import com.datn.datn_be.service.ProductService;
import com.datn.datn_be.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/public")
@CrossOrigin(origins = "*")
public class PublicController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;



    @GetMapping("/hierarchy/all")
    public ResponseEntity<ApiResponse<List<CategoryDetailResponse>>> getAllCategoriesWithHierarchy() {
        try {
            List<CategoryDetailResponse> response = categoryService.getAllCategoriesWithHierarchy();
            return ResponseEntity.ok(new ApiResponse<>(0, "Categories with hierarchy retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }


    @GetMapping("/product/list")
    public ResponseEntity<ApiResponse<PaginationResponse<ProductResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        try {
            PaginationResponse<ProductResponse> response = productService.getAllProducts(page, pageSize, sortBy, sortDirection);
            return ResponseEntity.ok(new ApiResponse<>(0, "Products retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/product/search")
    public ResponseEntity<ApiResponse<PaginationResponse<ProductResponse>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PaginationResponse<ProductResponse> response = productService.searchProducts(keyword, page, pageSize);
            return ResponseEntity.ok(new ApiResponse<>(0, "Products searched successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/product/category/{categoryId}")
    public ResponseEntity<ApiResponse<PaginationResponse<ProductResponse>>> getProductsByCategory(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PaginationResponse<ProductResponse> response = productService.getProductsByCategory(categoryId, page, pageSize);
            return ResponseEntity.ok(new ApiResponse<>(0, "Products by category retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/product/filter/brand")
    public ResponseEntity<ApiResponse<PaginationResponse<ProductResponse>>> filterProductsByBrand(
            @RequestParam String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PaginationResponse<ProductResponse> response = productService.filterProductsByBrand(brand, page, pageSize);
            return ResponseEntity.ok(new ApiResponse<>(0, "Products filtered by brand successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/product/filter/price")
    public ResponseEntity<ApiResponse<PaginationResponse<ProductResponse>>> filterProductsByPriceRange(
            @RequestParam java.math.BigDecimal minPrice,
            @RequestParam java.math.BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PaginationResponse<ProductResponse> response = productService.filterProductsByPriceRange(minPrice, maxPrice, page, pageSize);
            return ResponseEntity.ok(new ApiResponse<>(0, "Products filtered by price successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }


    @GetMapping("product/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable String productId) {
        try {
            ProductResponse response = productService.getProductById(productId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Product retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        }
    }

    /** Lấy đánh giá theo sản phẩm (public) */
    @GetMapping("/review/product/{productId}")
    public ResponseEntity<ApiResponse<PaginationResponse<ReviewResponse>>> getReviewsByProduct(
            @PathVariable String productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PaginationResponse<ReviewResponse> response = reviewService.getReviewsByProduct(productId, page, pageSize);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy đánh giá thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Lấy tóm tắt đánh giá theo sản phẩm (public) */
    @GetMapping("/review/product/{productId}/summary")
    public ResponseEntity<ApiResponse<ReviewSummaryResponse>> getReviewSummary(
            @PathVariable String productId) {
        try {
            ReviewSummaryResponse response = reviewService.getSummaryByProduct(productId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy thống kê đánh giá thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/category/hierarchy/root")
    public ResponseEntity<ApiResponse<List<CategoryDetailResponse>>> getRootCategoriesWithSubcategories() {
        try {
            List<CategoryDetailResponse> response = categoryService.getRootCategoriesWithSubcategories();
            return ResponseEntity.ok(new ApiResponse<>(0, "Root categories with subcategories retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/product/filter/category-price")
    public ResponseEntity<ApiResponse<PaginationResponse<ProductResponse>>> filterProductsByCategoryAndPrice(
            @RequestParam Integer categoryId,
            @RequestParam java.math.BigDecimal minPrice,
            @RequestParam java.math.BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PaginationResponse<ProductResponse> response = productService.filterProductsByCategoryAndPrice(categoryId, minPrice, maxPrice, page, pageSize);
            return ResponseEntity.ok(new ApiResponse<>(0, "Products filtered by category and price successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
