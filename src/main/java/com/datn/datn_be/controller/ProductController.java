package com.datn.datn_be.controller;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.dto.CreateProductRequest;
import com.datn.datn_be.dto.PaginationResponse;
import com.datn.datn_be.dto.ProductImageAddRequest;
import com.datn.datn_be.dto.ProductImageResponse;
import com.datn.datn_be.dto.ProductImagesReplaceRequest;
import com.datn.datn_be.dto.ProductResponse;
import com.datn.datn_be.dto.UpdateProductRequest;
import com.datn.datn_be.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/product")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody CreateProductRequest request) {
        try {
            ProductResponse response = productService.createProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(0, "Product created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable String productId) {
        try {
            ProductResponse response = productService.getProductById(productId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Product retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        }
    }

    @GetMapping("/list")
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

    @GetMapping("/search")
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

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable String productId,
            @RequestBody UpdateProductRequest request) {
        try {
            ProductResponse response = productService.updateProduct(productId, request);
            return ResponseEntity.ok(new ApiResponse<>(0, "Product updated successfully", response));
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(409, "Sản phẩm đã bị thay đổi hoặc xóa bởi thao tác khác, vui lòng tải lại trang", null));
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Product deleted successfully", null));
        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(409, "Sản phẩm đã bị thay đổi hoặc xóa bởi thao tác khác", null));
        } catch (Exception e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(404, e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @GetMapping("/filter/status")
    public ResponseEntity<ApiResponse<PaginationResponse<ProductResponse>>> filterProductsByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PaginationResponse<ProductResponse> response = productService.filterProductsByStatus(status, page, pageSize);
            return ResponseEntity.ok(new ApiResponse<>(0, "Products filtered by status successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    // ── Image management endpoints ────────────────────────────────────────────

    @GetMapping("/{productId}/images")
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> getProductImages(
            @PathVariable String productId) {
        try {
            List<ProductImageResponse> images = productService.getProductImages(productId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Images retrieved successfully", images));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PostMapping("/{productId}/images")
    public ResponseEntity<ApiResponse<ProductImageResponse>> addProductImage(
            @PathVariable String productId,
            @RequestBody ProductImageAddRequest request) {
        try {
            ProductImageResponse image = productService.addProductImage(productId, request.getImageUrl());
            return ResponseEntity.ok(new ApiResponse<>(0, "Image added successfully", image));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductImage(
            @PathVariable String productId,
            @PathVariable Integer imageId) {
        try {
            productService.deleteProductImage(imageId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Image deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    @PutMapping("/{productId}/images")
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> replaceProductImages(
            @PathVariable String productId,
            @RequestBody ProductImagesReplaceRequest request) {
        try {
            List<ProductImageResponse> images = productService.replaceProductImages(productId, request.getImageUrls());
            return ResponseEntity.ok(new ApiResponse<>(0, "Images replaced successfully", images));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

}
