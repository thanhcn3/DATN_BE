package com.datn.datn_be.controller;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.dto.PaginationResponse;
import com.datn.datn_be.dto.RegisterRequest;
import com.datn.datn_be.dto.RegisterResponse;
import com.datn.datn_be.dto.UpdateUserRequest;
import com.datn.datn_be.dto.UserResponse;
import com.datn.datn_be.entity.User;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.repository.UserRepository;
import com.datn.datn_be.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AdminController - endpoints reserved for admin operations.
 * All routes under /v1/admin require a valid JWT (not under /v1/public).
 * The JwtAuthenticationFilter enforces authentication automatically.
 */
@RestController
@RequestMapping("/v1/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    /**
     * POST /v1/admin/register
     * Register a new admin user (ROLE_ADMIN).
     * Requires: Authorization: Bearer <token> (must be a logged-in admin)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerAdmin(
            @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.registerAdmin(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(0, "Admin registered successfully", response));
    }

    /**
     * GET /v1/admin/users
     * List all users with pagination, keyword search and status filter.
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PaginationResponse<UserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userPage = userRepository.searchUsers(keyword, status, pageable);

        List<UserResponse> content = userPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        PaginationResponse<UserResponse> result = new PaginationResponse<>(
                (int) userPage.getNumber(),
                pageSize,
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                content,
                userPage.hasNext(),
                userPage.hasPrevious()
        );

        return ResponseEntity.ok(new ApiResponse<>(0, "Users retrieved successfully", result));
    }

    /**
     * GET /v1/admin/users/{userId}
     * Get a single user's details.
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String userId) {
        UUID uuid = UUID.fromString(userId);
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new ClientSideException(404, "User not found"));
        return ResponseEntity.ok(new ApiResponse<>(0, "User retrieved successfully", mapToResponse(user)));
    }

    /**
     * PUT /v1/admin/users/{userId}
     * Update user info (fullName, email, phone).
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String userId,
            @RequestBody UpdateUserRequest body) {

        UUID uuid = UUID.fromString(userId);
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new ClientSideException(404, "User not found"));

        if (body.getEmail() != null && !body.getEmail().isBlank()) {
            if (!body.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(body.getEmail())) {
                throw new ClientSideException(400, "Email đã được sử dụng");
            }
            user.setEmail(body.getEmail().trim());
        }
        if (body.getFullName() != null && !body.getFullName().isBlank()) {
            user.setFullName(body.getFullName().trim());
        }
        if (body.getPhone() != null && !body.getPhone().isBlank()) {
            if (!body.getPhone().equals(user.getPhone()) && userRepository.existsByPhone(body.getPhone())) {
                throw new ClientSideException(400, "Số điện thoại đã được sử dụng");
            }
            user.setPhone(body.getPhone().trim());
        }

        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse<>(0, "Cập nhật thông tin thành công", mapToResponse(user)));
    }

    /**
     * PUT /v1/admin/users/{userId}/status
     * Update a user's status (ACTIVE / INACTIVE / BANNED).
     * Body: { "status": "ACTIVE" }
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {

        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(400, "status field is required", null));
        }

        UUID uuid = UUID.fromString(userId);
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new ClientSideException(404, "User not found"));

        user.setStatus(newStatus.toUpperCase());
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse<>(0, "User status updated", mapToResponse(user)));
    }

    // ── helpers ──────────────────────────────────────────────
    private UserResponse mapToResponse(User u) {
        UserResponse r = new UserResponse();
        r.setUserId(u.getId().toString());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setFullName(u.getFullName());
        r.setPhone(u.getPhone());
        r.setStatus(u.getStatus());
        r.setCreatedAt(u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
        r.setUpdatedAt(u.getUpdatedAt() != null ? u.getUpdatedAt().toString() : null);
        return r;
    }
}

