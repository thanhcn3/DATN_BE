package com.datn.datn_be.repository;

import com.datn.datn_be.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR u.status = :status)")
    Page<User> searchUsers(@Param("keyword") String keyword,
                           @Param("status") String status,
                           Pageable pageable);
}

