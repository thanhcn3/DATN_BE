package com.datn.datn_be.repository;

import com.datn.datn_be.entity.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

    Optional<ChatRoom> findFirstByUserIdAndStatusOrderByUpdatedAtDesc(UUID userId, String status);

    Optional<ChatRoom> findFirstByUserIdOrderByUpdatedAtDesc(UUID userId);

    Optional<ChatRoom> findByIdAndUserId(UUID roomId, UUID userId);

    Page<ChatRoom> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    Page<ChatRoom> findByStatusOrderByUpdatedAtDesc(String status, Pageable pageable);

    long countByUnreadAdminCountGreaterThan(int unreadAdminCount);

    long countByUserIdAndStatusAndUnreadUserCountGreaterThan(UUID userId, String status, int unreadUserCount);
}
