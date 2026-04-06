package com.datn.datn_be.repository;

import com.datn.datn_be.entity.AppNotification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, UUID> {

    List<AppNotification> findByRecipientTypeOrderByCreatedAtDesc(String recipientType, Pageable pageable);

    List<AppNotification> findByRecipientTypeAndRecipientUserIdOrderByCreatedAtDesc(String recipientType, UUID recipientUserId, Pageable pageable);

    List<AppNotification> findByRecipientTypeAndIsReadFalseOrderByCreatedAtDesc(String recipientType, Pageable pageable);

    List<AppNotification> findByRecipientTypeAndRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(
            String recipientType,
            UUID recipientUserId,
            Pageable pageable
    );

    long countByRecipientTypeAndIsReadFalse(String recipientType);

    long countByRecipientTypeAndRecipientUserIdAndIsReadFalse(String recipientType, UUID recipientUserId);

    Optional<AppNotification> findByIdAndRecipientType(UUID id, String recipientType);

    Optional<AppNotification> findByIdAndRecipientTypeAndRecipientUserId(UUID id, String recipientType, UUID recipientUserId);

    List<AppNotification> findByRecipientTypeAndIsReadFalse(String recipientType);

    List<AppNotification> findByRecipientTypeAndRecipientUserIdAndIsReadFalse(String recipientType, UUID recipientUserId);
}
