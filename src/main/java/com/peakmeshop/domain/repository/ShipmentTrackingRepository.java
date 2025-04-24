package com.peakmeshop.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.ShipmentTracking;

@Repository
public interface ShipmentTrackingRepository extends JpaRepository<ShipmentTracking, Long> {

    List<ShipmentTracking> findByShipmentIdOrderByStatusChangedAtDesc(Long shipmentId);

    List<ShipmentTracking> findByShipmentTrackingNumberOrderByStatusChangedAtDesc(String trackingNumber);

    void deleteByShipmentId(Long shipmentId);
}