package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.InventoryDTO;
import com.peakmeshop.api.dto.InventoryHistoryDTO;
import com.peakmeshop.domain.entity.Inventory;
import com.peakmeshop.domain.entity.InventoryHistory;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.repository.InventoryHistoryRepository;
import com.peakmeshop.domain.repository.InventoryRepository;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.service.InventoryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Override
    public Optional<InventoryDTO> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public InventoryDTO updateInventory(Long productId, int quantity, String reason, Long userId) {
        Inventory inventory = getOrCreateInventory(productId);
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        int oldQuantity = inventory.getQuantity();
        inventory.setQuantity(quantity);
        inventory.setUpdatedAt(LocalDateTime.now());

        InventoryHistory history = InventoryHistory.builder()
                .inventory(inventory)
                .quantity(quantity - oldQuantity)
                .reason(reason)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        inventoryHistoryRepository.save(history);
        return convertToDTO(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryDTO increaseInventory(Long productId, int quantity, String reason, Long userId) {
        Inventory inventory = getOrCreateInventory(productId);
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setUpdatedAt(LocalDateTime.now());

        InventoryHistory history = InventoryHistory.builder()
                .inventory(inventory)
                .quantity(quantity)
                .reason(reason)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        inventoryHistoryRepository.save(history);
        return convertToDTO(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryDTO decreaseInventory(Long productId, int quantity, String reason, Long userId) {
        Inventory inventory = getOrCreateInventory(productId);
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (inventory.getQuantity() < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setUpdatedAt(LocalDateTime.now());

        InventoryHistory history = InventoryHistory.builder()
                .inventory(inventory)
                .quantity(-quantity)
                .reason(reason)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        inventoryHistoryRepository.save(history);
        return convertToDTO(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public boolean reserveInventory(Long productId, int quantity, Long orderId) {
        Inventory inventory = getOrCreateInventory(productId);

        if (inventory.getQuantity() - inventory.getReservedQuantity() < quantity) {
            return false;
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        return true;
    }

    @Override
    @Transactional
    public boolean releaseReservedInventory(Long productId, int quantity, Long orderId) {
        Inventory inventory = getOrCreateInventory(productId);

        if (inventory.getReservedQuantity() < quantity) {
            return false;
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        return true;
    }

    @Override
    @Transactional
    public boolean confirmReservedInventory(Long productId, int quantity, Long orderId) {
        Inventory inventory = getOrCreateInventory(productId);

        if (inventory.getReservedQuantity() < quantity || inventory.getQuantity() < quantity) {
            return false;
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        return true;
    }

    @Override
    public List<InventoryDTO> getLowStockProducts() {
        return inventoryRepository.findByQuantityLessThanOrEqualToThreshold()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public Page<InventoryDTO> getAllInventory(Pageable pageable) {
        return inventoryRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public Page<InventoryHistoryDTO> getInventoryHistory(Long productId, Pageable pageable) {
        return inventoryHistoryRepository.findByInventoryProductId(productId, pageable)
                .map(this::convertToHistoryDTO);
    }

    @Override
    public Page<InventoryHistoryDTO> getAllInventoryHistory(Pageable pageable) {
        return inventoryHistoryRepository.findAll(pageable)
                .map(this::convertToHistoryDTO);
    }

    @Override
    @Transactional
    public void updateLowStockThreshold(Long productId, int threshold) {
        Inventory inventory = getOrCreateInventory(productId);
        inventory.setLowStockThreshold(threshold);
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);
    }

    @Override
    public boolean isProductInStock(Long productId, int quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);
        return inventoryOpt.map(inventory -> 
            inventory.getQuantity() - inventory.getReservedQuantity() >= quantity
        ).orElse(false);
    }

    @Override
    public int getAvailableQuantity(Long productId) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);
        return inventoryOpt.map(inventory -> 
            inventory.getQuantity() - inventory.getReservedQuantity()
        ).orElse(0);
    }

    private Inventory getOrCreateInventory(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

                    Inventory newInventory = Inventory.builder()
                            .product(product)
                            .quantity(0)
                            .reservedQuantity(0)
                            .lowStockThreshold(10)
                            .createdAt(LocalDateTime.now())
                            .build();

                    return inventoryRepository.save(newInventory);
                });
    }

    private InventoryDTO convertToDTO(Inventory inventory) {
        return InventoryDTO.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .lowStockThreshold(inventory.getLowStockThreshold())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    private InventoryHistoryDTO convertToHistoryDTO(InventoryHistory history) {
        return InventoryHistoryDTO.builder()
                .id(history.getId())
                .inventoryId(history.getInventory().getId())
                .productId(history.getInventory().getProduct().getId())
                .productName(history.getInventory().getProduct().getName())
                .quantity(history.getQuantity())
                .reason(history.getReason())
                .userId(history.getUser().getId())
                .userName(history.getUser().getName())
                .createdAt(history.getCreatedAt())
                .build();
    }
}