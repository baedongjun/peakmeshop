package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;

import com.peakmeshop.api.dto.ShippingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShippingService {

    Page<ShippingDTO> getAllShippings(Pageable pageable);

    Optional<ShippingDTO> getShippingById(Long id);

    Optional<ShippingDTO> getShippingByOrderId(Long orderId);

    Optional<ShippingDTO> getShippingByTrackingNumber(String trackingNumber);

    ShippingDTO createShipping(ShippingDTO shippingDTO);

    ShippingDTO updateShippingStatus(Long id, String status);

    ShippingDTO updateTrackingInfo(Long id, String carrier, String trackingNumber);

    List<ShippingDTO> getShippingsByStatus(String status, Pageable pageable);
}