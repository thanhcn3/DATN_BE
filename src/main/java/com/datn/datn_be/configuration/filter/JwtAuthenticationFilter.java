package com.datn.datn_be.configuration.filter;

import com.datn.datn_be.dto.ApiResponse;
import com.datn.datn_be.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();

        try {
            if (isBypassPath(request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = extractJwtFromRequest(request);

            if (jwt != null && jwtUtil.validateToken(jwt)) {
                // Check if token is blacklisted in Redis (logout)
                String tokenKey = "jwt_token:" + jwt;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey))) {
                    // Token is blacklisted, skip authentication
                    filterChain.doFilter(request, response);
                    return;
                }

                String userId = jwtUtil.getUserIdFromToken(jwt);
                String username = jwtUtil.getUsernameFromToken(jwt);

                logger.info("AUTH {} {} - User: {} ({})", method, uri, username, userId);

                // Create authentication token
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                new ArrayList<>()
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
                return;
            } else {
                logger.warn("AUTH {} {} - Invalid/Missing token", method, uri);
            }
        } catch (Exception ex) {
            logger.error("AUTH {} {} - Error: {}", method, uri, ex.getMessage());
        }

        writeErrorResponse(response, "Token không hợp lệ hoặc đã hết hạn");
    }

    @Autowired
    private ObjectMapper objectMapper;

    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = new ApiResponse<>(401, message);

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    /**
     * Extract JWT from Authorization header
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isBypassPath(String path) {
        return path.contains("/api/be-service/v1/public")
                || path.contains("/v1/order/vnpay-verify");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip JWT check for CORS preflight requests
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}
