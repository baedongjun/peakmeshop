package com.peakmeshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.peakmeshop.entity.Shipping;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    Optional<Shipping> findByOrderId(Long orderId);

    Optional<Shipping> findByTrackingNumber(String trackingNumber);

    boolean existsByOrderId(Long orderId);

    List<Shipping> findByStatus(String status, Pageable pageable);
}