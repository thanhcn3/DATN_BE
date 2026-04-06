package com.datn.datn_be.repository;

import com.datn.datn_be.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    java.util.Optional<Payment> findByOrderId(UUID orderId);
    List<Payment> findByStatus(String status);
}

