package com.peakmeshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peakmeshop.entity.ShipmentTracking;

@Repository
public interface ShipmentTrackingRepository extends JpaRepository<ShipmentTracking, Long> {

    List<ShipmentTracking> findByShipmentIdOrderByStatusChangedAtDesc(Long shipmentId);

    void deleteByShipmentId(Long shipmentId);
}