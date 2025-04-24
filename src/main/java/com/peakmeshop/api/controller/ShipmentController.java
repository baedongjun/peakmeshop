package com.peakmeshop.api.controller;

import java.util.List;

import com.peakmeshop.domain.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.api.dto.ShipmentDTO;
import com.peakmeshop.api.dto.ShipmentTrackingDTO;
import com.peakmeshop.domain.service.ShipmentService;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ShipmentDTO>> getAllShipments(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(shipmentService.getAllShipments(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentDTO> getShipmentById(@PathVariable Long id) {
        return shipmentService.getShipmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ShipmentDTO>> getShipmentsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(shipmentService.getShipmentsByOrderId(orderId));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ShipmentDTO> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        return shipmentService.getShipmentByTrackingNumber(trackingNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShipmentDTO> createShipment(@RequestBody ShipmentDTO shipmentDTO) {
        return new ResponseEntity<>(shipmentService.createShipment(shipmentDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShipmentDTO> updateShipment(
            @PathVariable Long id,
            @RequestBody ShipmentDTO shipmentDTO) {
        return ResponseEntity.ok(shipmentService.updateShipment(id, shipmentDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        if (shipmentService.deleteShipment(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShipmentDTO> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam ShipmentStatus status) {
        return ResponseEntity.ok(shipmentService.updateShipmentStatus(id, status));
    }

    @PostMapping("/{shipmentId}/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShipmentTrackingDTO> addTrackingEvent(
            @PathVariable Long shipmentId,
            @RequestBody ShipmentTrackingDTO trackingDTO) {
        return new ResponseEntity<>(
                shipmentService.addTrackingEvent(shipmentId, trackingDTO),
                HttpStatus.CREATED);
    }

    @GetMapping("/{shipmentId}/tracking")
    public ResponseEntity<List<ShipmentTrackingDTO>> getTrackingHistory(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(shipmentService.getTrackingHistory(shipmentId));
    }

    @GetMapping("/tracking/{trackingNumber}/history")
    public ResponseEntity<List<ShipmentTrackingDTO>> getTrackingHistoryByTrackingNumber(
            @PathVariable String trackingNumber) {
        return ResponseEntity.ok(shipmentService.getTrackingHistoryByTrackingNumber(trackingNumber));
    }

    @PostMapping("/{shipmentId}/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> syncTrackingInformation(@PathVariable Long shipmentId) {
        shipmentService.syncTrackingInformation(shipmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sync-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> syncAllTrackingInformation() {
        shipmentService.syncAllTrackingInformation();
        return ResponseEntity.ok().build();
    }
}