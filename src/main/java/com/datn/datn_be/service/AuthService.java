package com.datn.datn_be.service;

import com.datn.datn_be.dto.LoginRequest;
import com.datn.datn_be.dto.LoginResponse;
import com.datn.datn_be.dto.RefreshTokenRequest;
import com.datn.datn_be.dto.RegisterRequest;
import com.datn.datn_be.dto.RegisterResponse;
import com.datn.datn_be.entity.User;
import com.datn.datn_be.entity.UserRole;
import com.datn.datn_be.exception.ClientSideException;
import com.datn.datn_be.repository.UserRepository;
import com.datn.datn_be.repository.UserRoleRepository;
import com.datn.datn_be.util.JwtUtil;
import com.datn.datn_be.configuration.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtConfig jwtConfig;

    /**
     * User login - authenticate user and return JWT token
     * Supports login by username, email, or phone number
     */
    public LoginResponse login(LoginRequest loginRequest) {
        String identifier = loginRequest.getUsername();

        Optional<User> userOptional;

        // Detect login type: email (contains @), phone (digits only, 9-11 chars), or username
        if (identifier != null && identifier.contains("@")) {
            userOptional = userRepository.findByEmail(identifier);
        } else if (identifier != null && identifier.matches("^\\d{9,11}$")) {
            userOptional = userRepository.findByPhone(identifier);
        } else {
            userOptional = userRepository.findByUsername(identifier);
        }

        if (userOptional.isEmpty()) {
            throw new ClientSideException(401, "Tài khoản không tồn tại");
        }

        User user = userOptional.get();

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new ClientSideException(401, "Mật khẩu không chính xác");
        }

        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        // Store tokens in Redis for verification
        String accessTokenKey = "jwt_token:" + accessToken;
        String refreshTokenKey = "jwt_refresh_token:" + refreshToken;

        redisTemplate.opsForValue().set(accessTokenKey, user.getId().toString(), jwtConfig.getExpiration(), TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(refreshTokenKey, user.getId().toString(), jwtConfig.getRefreshExpiration(), TimeUnit.MILLISECONDS);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUserId(user.getId().toString());
        response.setUsername(user.getUsername());
        response.setExpiresIn(jwtConfig.getExpiration());

        return response;
    }

    /**
     * User registration - create new user account
     */
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        // Validate request data
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new ClientSideException(400, "Username is required");
        }

        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            throw new ClientSideException(400, "Password is required");
        }

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new ClientSideException(400, "Password and confirm password do not match");
        }

        if (registerRequest.getPassword().length() < 6) {
            throw new ClientSideException(400, "Password must be at least 6 characters long");
        }

        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new ClientSideException(400, "Email is required");
        }

        // Validate email format
        if (!registerRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ClientSideException(400, "Invalid email format");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ClientSideException(409, "Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ClientSideException(409, "Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setFullName(registerRequest.getFullName());
        user.setPhone(registerRequest.getPhone());
        user.setStatus("ACTIVE");

        // Save user to database
        User savedUser = userRepository.save(user);

        UserRole newRole = new UserRole();
        newRole.setUserId(savedUser.getId());
        newRole.setRoleId(3);

        userRoleRepository.save(newRole);
        // Prepare response
        RegisterResponse response = new RegisterResponse();
        response.setUserId(savedUser.getId().toString());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setFullName(savedUser.getFullName());
        response.setMessage("User registered successfully");

        return response;
    }

    /**
     * Admin registration - create new admin account (roleId = 1 = ROLE_ADMIN)
     * Should only be called by an already-authenticated admin.
     */
    @Transactional
    public RegisterResponse registerAdmin(RegisterRequest registerRequest) {
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new ClientSideException(400, "Username is required");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            throw new ClientSideException(400, "Password is required");
        }
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new ClientSideException(400, "Password and confirm password do not match");
        }
        if (registerRequest.getPassword().length() < 6) {
            throw new ClientSideException(400, "Password must be at least 6 characters long");
        }
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new ClientSideException(400, "Email is required");
        }
        if (!registerRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ClientSideException(400, "Invalid email format");
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ClientSideException(409, "Username already exists");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ClientSideException(409, "Email already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setFullName(registerRequest.getFullName());
        user.setPhone(registerRequest.getPhone());
        user.setStatus("ACTIVE");

        User savedUser = userRepository.save(user);

        // Assign ROLE_ADMIN (roleId = 1)
        UserRole adminRole = new UserRole();
        adminRole.setUserId(savedUser.getId());
        adminRole.setRoleId(1);
        userRoleRepository.save(adminRole);

        RegisterResponse response = new RegisterResponse();
        response.setUserId(savedUser.getId().toString());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setFullName(savedUser.getFullName());
        response.setMessage("Admin registered successfully");

        return response;
    }

    /**
     * Refresh access token using refresh token
     */
    public LoginResponse refreshToken(RefreshTokenRequest request){
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new ClientSideException("Invalid refresh token");
        }

        // Check if refresh token exists in Redis
        String refreshTokenKey = "jwt_refresh_token:" + refreshToken;
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(refreshTokenKey))) {
            throw new ClientSideException("Refresh token not found or expired");
        }

        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(java.util.UUID.fromString(userId), username);

        // Store new token in Redis
        String accessTokenKey = "jwt_token:" + newAccessToken;
        redisTemplate.opsForValue().set(accessTokenKey, userId, jwtConfig.getExpiration(), TimeUnit.MILLISECONDS);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(refreshToken);
        response.setUserId(userId);
        response.setUsername(username);
        response.setExpiresIn(jwtConfig.getExpiration());

        return response;
    }

    /**
     * Logout - blacklist the token in Redis
     */
    public void logout(String token) throws Exception {
        if (!jwtUtil.validateToken(token)) {
            throw new Exception("Invalid token");
        }

        // Blacklist the token by storing it with a key that indicates it's blacklisted
        String tokenKey = "jwt_token:" + token;
        long remainingTime = getRemainingTimeFromToken(token);

        if (remainingTime > 0) {
            redisTemplate.opsForValue().set(tokenKey, "blacklisted", remainingTime, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Get remaining time until token expiration
     */
    private long getRemainingTimeFromToken(String token) {
        try {
            long expTime = jwtUtil.getClaimsFromToken(token).getExpiration().getTime();
            return expTime - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }
}

