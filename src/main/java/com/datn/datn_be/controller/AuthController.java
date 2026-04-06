package com.datn.datn_be.controller;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.dto.LoginRequest;
import com.datn.datn_be.dto.LoginResponse;
import com.datn.datn_be.dto.RefreshTokenRequest;
import com.datn.datn_be.dto.RegisterRequest;
import com.datn.datn_be.dto.RegisterResponse;
import com.datn.datn_be.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/public/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(new ApiResponse<>(0, "Login successful", response));
    }

    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(0, "User registered successfully", response));

    }

    /**
     * Refresh access token endpoint
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
            LoginResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(new ApiResponse<>(0, "Token refreshed successfully", response));

    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            authService.logout(token);
            return ResponseEntity.ok(new ApiResponse<>(0, "Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, e.getMessage()));
        }
    }

    /**
     * Admin registration endpoint (public - no auth required)
     * POST /v1/public/auth/register-admin
     */
    @PostMapping("/register-admin")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerAdmin(@RequestBody RegisterRequest request) {
        RegisterResponse response = authService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(0, "Admin registered successfully", response));
    }

    /**
     * Test endpoint - requires authentication
     */
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(new ApiResponse<>(0, "Authenticated request successful", "You are authenticated!"));
    }
}

