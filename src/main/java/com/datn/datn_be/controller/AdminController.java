package com.datn.datn_be.controller;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.dto.RegisterRequest;
import com.datn.datn_be.dto.RegisterResponse;
import com.datn.datn_be.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
