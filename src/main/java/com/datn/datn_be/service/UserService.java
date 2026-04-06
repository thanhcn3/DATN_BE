package com.datn.datn_be.service;

import com.datn.datn_be.dto.ChangePasswordRequest;
import com.datn.datn_be.dto.UpdateUserRequest;
import com.datn.datn_be.dto.UserResponse;
import com.datn.datn_be.entity.User;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.repository.UserRepository;
import com.datn.datn_be.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get user information by token
     */
    public UserResponse getUserByToken(String token) {
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new ClientSideException(401, "Invalid token");
        }

        UUID userUUID = UUID.fromString(userId);
        Optional<User> userOptional = userRepository.findById(userUUID);

        if (userOptional.isEmpty()) {
            throw new ClientSideException(404, "User not found");
        }

        return mapUserToResponse(userOptional.get());
    }

    /**
     * Get user information by userId
     */
    public UserResponse getUserById(String userId) {
        UUID userUUID = UUID.fromString(userId);
        Optional<User> userOptional = userRepository.findById(userUUID);

        if (userOptional.isEmpty()) {
            throw new ClientSideException(404, "User not found");
        }

        return mapUserToResponse(userOptional.get());
    }

    /**
     * Update user information
     */
    @Transactional
    public UserResponse updateUser(String token, UpdateUserRequest request) {
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new ClientSideException(401, "Invalid token");
        }

        UUID userUUID = UUID.fromString(userId);
        Optional<User> userOptional = userRepository.findById(userUUID);

        if (userOptional.isEmpty()) {
            throw new ClientSideException(404, "User not found");
        }

        User user = userOptional.get();

        // Update fields
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail()) &&
                !user.getEmail().equals(request.getEmail())) {
                throw new ClientSideException(400, "Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }

        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);

        return mapUserToResponse(updatedUser);
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(String token, ChangePasswordRequest request) {
        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new ClientSideException(401, "Invalid token");
        }

        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ClientSideException(400, "New password and confirm password do not match");
        }

        // Validate password length
        if (request.getNewPassword().length() < 6) {
            throw new ClientSideException(400, "Password must be at least 6 characters");
        }

        UUID userUUID = UUID.fromString(userId);
        Optional<User> userOptional = userRepository.findById(userUUID);

        if (userOptional.isEmpty()) {
            throw new ClientSideException(404, "User not found");
        }

        User user = userOptional.get();

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new ClientSideException(401, "Old password is incorrect");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    /**
     * Map User entity to UserResponse DTO
     */
    private UserResponse mapUserToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getId().toString());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        response.setUpdatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null);
        return response;
    }
}

