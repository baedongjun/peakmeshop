package com.peakmeshop.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Shipment;
import com.peakmeshop.domain.enums.ShipmentStatus;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    List<Shipment> findByOrderId(Long orderId);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);

    List<Shipment> findByStatusIn(List<ShipmentStatus> statuses);
}