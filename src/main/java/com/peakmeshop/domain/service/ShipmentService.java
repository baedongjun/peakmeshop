package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;

import com.peakmeshop.domain.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.ShipmentDTO;
import com.peakmeshop.api.dto.ShipmentTrackingDTO;

public interface ShipmentService {

    Page<ShipmentDTO> getAllShipments(Pageable pageable);

    Optional<ShipmentDTO> getShipmentById(Long id);

    List<ShipmentDTO> getShipmentsByOrderId(Long orderId);

    Optional<ShipmentDTO> getShipmentByTrackingNumber(String trackingNumber);

    ShipmentDTO createShipment(ShipmentDTO shipmentDTO);

    ShipmentDTO updateShipment(Long id, ShipmentDTO shipmentDTO);

    boolean deleteShipment(Long id);

    ShipmentDTO updateShipmentStatus(Long id, ShipmentStatus status);

    ShipmentTrackingDTO addTrackingEvent(Long shipmentId, ShipmentTrackingDTO trackingDTO);

    List<ShipmentTrackingDTO> getTrackingHistory(Long shipmentId);

    List<ShipmentTrackingDTO> getTrackingHistoryByTrackingNumber(String trackingNumber);

    void syncTrackingInformation(Long shipmentId);

    void syncAllTrackingInformation();
}