package com.peakmeshop.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.api.dto.InventoryDTO;
import com.peakmeshop.api.dto.InventoryHistoryDTO;

public interface InventoryService {

    Optional<InventoryDTO> getInventoryByProductId(Long productId);

    InventoryDTO updateInventory(Long productId, int quantity, String reason, Long userId);

    InventoryDTO increaseInventory(Long productId, int quantity, String reason, Long userId);

    InventoryDTO decreaseInventory(Long productId, int quantity, String reason, Long userId);

    boolean reserveInventory(Long productId, int quantity, Long orderId);

    boolean releaseReservedInventory(Long productId, int quantity, Long orderId);

    boolean confirmReservedInventory(Long productId, int quantity, Long orderId);

    List<InventoryDTO> getLowStockProducts();

    Page<InventoryDTO> getAllInventory(Pageable pageable);

    Page<InventoryHistoryDTO> getInventoryHistory(Long productId, Pageable pageable);

    Page<InventoryHistoryDTO> getAllInventoryHistory(Pageable pageable);

    void updateLowStockThreshold(Long productId, int threshold);

    boolean isProductInStock(Long productId, int quantity);

    int getAvailableQuantity(Long productId);
}