package com.datn.datn_be.service;

import com.datn.datn_be.dto.CreateReviewRequest;
import com.datn.datn_be.dto.PaginationResponse;
import com.datn.datn_be.dto.ReviewResponse;
import com.datn.datn_be.dto.ReviewSummaryResponse;
import com.datn.datn_be.entity.Review;
import com.datn.datn_be.entity.User;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.repository.ReviewRepository;
import com.datn.datn_be.repository.UserRepository;
import com.datn.datn_be.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private UUID getUserIdFromHeader(String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) throw new ClientSideException(401, "Token không hợp lệ");
        return UUID.fromString(userId);
    }

    /** Lấy danh sách đánh giá theo sản phẩm (public) */
    public PaginationResponse<ReviewResponse> getReviewsByProduct(String productId, int page, int pageSize) {
        UUID productUUID = UUID.fromString(productId);
        Page<Review> reviewPage = reviewRepository.findByProductIdOrderByCreatedAtDesc(
                productUUID, PageRequest.of(page, pageSize));

        return new PaginationResponse<>(
                page, pageSize,
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.getContent().stream().map(this::mapToResponse).toList(),
                reviewPage.hasNext(),
                reviewPage.hasPrevious()
        );
    }

    /** Lấy thống kê đánh giá theo sản phẩm (public) */
    public ReviewSummaryResponse getSummaryByProduct(String productId) {
        UUID productUUID = UUID.fromString(productId);
        Double avg = reviewRepository.getAverageRatingByProductId(productUUID);
        long total = reviewRepository.countByProductId(productUUID);
        return new ReviewSummaryResponse(
                avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0,
                total
        );
    }

    /** Đăng đánh giá mới (requires auth) */
    @Transactional
    public ReviewResponse createReview(String authHeader, String productId, CreateReviewRequest request) {
        UUID userId = getUserIdFromHeader(authHeader);
        UUID productUUID = UUID.fromString(productId);

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new ClientSideException(400, "Điểm đánh giá phải từ 1 đến 5");
        }
        if (request.getComment() == null || request.getComment().trim().isEmpty()) {
            throw new ClientSideException(400, "Vui lòng nhập nội dung đánh giá");
        }

        if (reviewRepository.findByProductIdAndUserId(productUUID, userId).isPresent()) {
            throw new ClientSideException(400, "Bạn đã đánh giá sản phẩm này rồi");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy người dùng"));

        Review review = new Review();
        review.setProductId(productUUID);
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setComment(request.getComment().trim());
        review.setCreatedAt(Instant.now());
        review.setUserFullName(user.getFullName() != null ? user.getFullName() : user.getUsername());

        return mapToResponse(reviewRepository.save(review));
    }

    /** Xóa đánh giá (admin hoặc chủ review) */
    @Transactional
    public void deleteReview(String authHeader, String reviewId) {
        UUID userId = getUserIdFromHeader(authHeader);
        UUID reviewUUID = UUID.fromString(reviewId);

        Review review = reviewRepository.findById(reviewUUID)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy đánh giá"));

        // Allow deletion by owner or accept any authenticated admin-level call
        if (!review.getUserId().equals(userId)) {
            throw new ClientSideException(403, "Bạn không có quyền xóa đánh giá này");
        }
        reviewRepository.delete(review);
    }

    /** Xóa đánh giá (admin - không kiểm tra ownership) */
    @Transactional
    public void adminDeleteReview(String reviewId) {
        UUID reviewUUID = UUID.fromString(reviewId);
        Review review = reviewRepository.findById(reviewUUID)
                .orElseThrow(() -> new ClientSideException(404, "Không tìm thấy đánh giá"));
        reviewRepository.delete(review);
    }

    /** Lấy tất cả đánh giá (admin) */
    public PaginationResponse<ReviewResponse> getAllReviews(int page, int pageSize) {
        Page<Review> reviewPage = reviewRepository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(page, pageSize));
        return new PaginationResponse<>(
                page, pageSize,
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.getContent().stream().map(this::mapToResponse).toList(),
                reviewPage.hasNext(),
                reviewPage.hasPrevious()
        );
    }

    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getProductId(),
                review.getUserId(),
                review.getUserFullName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
