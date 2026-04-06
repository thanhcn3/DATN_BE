package com.datn.datn_be.controller;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.dto.CreateReviewRequest;
import com.datn.datn_be.dto.PaginationResponse;
import com.datn.datn_be.dto.ReviewResponse;
import com.datn.datn_be.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/review")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /** Đăng đánh giá cho sản phẩm (yêu cầu đăng nhập) */
    @PostMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String productId,
            @RequestBody CreateReviewRequest request) {
        try {
            ReviewResponse response = reviewService.createReview(authHeader, productId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(0, "Đánh giá đã được gửi thành công", response));
        } catch (Exception e) {
            int status = e.getMessage().contains("401") ? 401
                    : e.getMessage().contains("đã đánh giá") ? 409 : 400;
            return ResponseEntity.status(status)
                    .body(new ApiResponse<>(status, e.getMessage(), null));
        }
    }

    /** Xóa đánh giá của chính mình (yêu cầu đăng nhập) */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String reviewId) {
        try {
            reviewService.deleteReview(authHeader, reviewId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Đã xóa đánh giá", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Lấy tất cả đánh giá (admin) */
    @GetMapping("/admin/list")
    public ResponseEntity<ApiResponse<PaginationResponse<ReviewResponse>>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            PaginationResponse<ReviewResponse> response = reviewService.getAllReviews(page, pageSize);
            return ResponseEntity.ok(new ApiResponse<>(0, "Lấy danh sách đánh giá thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }

    /** Xóa đánh giá bất kỳ (admin) */
    @DeleteMapping("/admin/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> adminDeleteReview(
            @PathVariable String reviewId) {
        try {
            reviewService.adminDeleteReview(reviewId);
            return ResponseEntity.ok(new ApiResponse<>(0, "Đã xóa đánh giá", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage(), null));
        }
    }
}
