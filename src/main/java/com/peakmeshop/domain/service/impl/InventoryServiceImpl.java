package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.InventoryDTO;
import com.peakmeshop.api.dto.InventoryHistoryDTO;
import com.peakmeshop.api.mapper.InventoryHistoryMapper;
import com.peakmeshop.api.mapper.InventoryMapper;
import com.peakmeshop.domain.entity.Inventory;
import com.peakmeshop.domain.entity.InventoryHistory;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.repository.InventoryHistoryRepository;
import com.peakmeshop.domain.repository.InventoryRepository;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryHistoryRepository inventoryHistoryRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final InventoryMapper inventoryMapper;
    private final InventoryHistoryMapper inventoryHistoryMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<InventoryDTO> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(this::convertToDTO);
    }

    @Override
    public InventoryDTO updateInventory(Long productId, int quantity, String reason, Long userId) {
        Inventory inventory = getOrCreateInventory(productId);
        int oldQuantity = inventory.getQuantity();

        inventory.setQuantity(quantity);
        Inventory savedInventory = inventoryRepository.save(inventory);

        // 재고 변경 이력 저장
        createInventoryHistory(inventory, oldQuantity, quantity, reason,
                quantity > oldQuantity ? "INCREASE" : "DECREASE", userId, null);

        log.info("재고 업데이트: 상품 ID={}, 이전 수량={}, 새 수량={}, 이유={}",
                productId, oldQuantity, quantity, reason);

        return convertToDTO(savedInventory);
    }

    @Override
    public InventoryDTO increaseInventory(Long productId, int quantity, String reason, Long userId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("증가시킬 수량은 0보다 커야 합니다.");
        }

        Inventory inventory = getOrCreateInventory(productId);
        int oldQuantity = inventory.getQuantity();
        int newQuantity = oldQuantity + quantity;

        inventory.setQuantity(newQuantity);
        Inventory savedInventory = inventoryRepository.save(inventory);

        // 재고 변경 이력 저장
        createInventoryHistory(inventory, oldQuantity, newQuantity, reason, "INCREASE", userId, null);

        log.info("재고 증가: 상품 ID={}, 이전 수량={}, 증가 수량={}, 새 수량={}, 이유={}",
                productId, oldQuantity, quantity, newQuantity, reason);

        return convertToDTO(savedInventory);
    }

    @Override
    public InventoryDTO decreaseInventory(Long productId, int quantity, String reason, Long userId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("감소시킬 수량은 0보다 커야 합니다.");
        }

        Inventory inventory = getOrCreateInventory(productId);
        int oldQuantity = inventory.getQuantity();

        if (oldQuantity < quantity) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + oldQuantity + ", 요청 수량: " + quantity);
        }

        int newQuantity = oldQuantity - quantity;
        inventory.setQuantity(newQuantity);
        Inventory savedInventory = inventoryRepository.save(inventory);

        // 재고 변경 이력 저장
        createInventoryHistory(inventory, oldQuantity, newQuantity, reason, "DECREASE", userId, null);

        log.info("재고 감소: 상품 ID={}, 이전 수량={}, 감소 수량={}, 새 수량={}, 이유={}",
                productId, oldQuantity, quantity, newQuantity, reason);

        return convertToDTO(savedInventory);
    }

    @Override
    public boolean reserveInventory(Long productId, int quantity, Long orderId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("예약할 수량은 0보다 커야 합니다.");
        }

        Inventory inventory = getOrCreateInventory(productId);
        int availableQuantity = inventory.getQuantity() - inventory.getReservedQuantity();

        if (availableQuantity < quantity) {
            log.warn("재고 예약 실패: 상품 ID={}, 가용 재고={}, 요청 수량={}",
                    productId, availableQuantity, quantity);
            return false;
        }

        int oldReservedQuantity = inventory.getReservedQuantity();
        int newReservedQuantity = oldReservedQuantity + quantity;

        inventory.setReservedQuantity(newReservedQuantity);
        inventoryRepository.save(inventory);

        // 재고 변경 이력 저장
        createInventoryHistory(inventory, availableQuantity, availableQuantity - quantity,
                "주문 예약", "RESERVE", null, orderId);

        log.info("재고 예약: 상품 ID={}, 가용 재고={}, 예약 수량={}, 주문 ID={}",
                productId, availableQuantity, quantity, orderId);

        return true;
    }

    @Override
    public boolean releaseReservedInventory(Long productId, int quantity, Long orderId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("해제할 수량은 0보다 커야 합니다.");
        }

        Inventory inventory = getOrCreateInventory(productId);

        if (inventory.getReservedQuantity() < quantity) {
            log.warn("예약 재고 해제 실패: 상품 ID={}, 예약 재고={}, 요청 수량={}",
                    productId, inventory.getReservedQuantity(), quantity);
            return false;
        }

        int oldReservedQuantity = inventory.getReservedQuantity();
        int newReservedQuantity = oldReservedQuantity - quantity;

        inventory.setReservedQuantity(newReservedQuantity);
        inventoryRepository.save(inventory);

        int availableQuantity = inventory.getQuantity() - oldReservedQuantity;

        // 재고 변경 이력 저장
        createInventoryHistory(inventory, availableQuantity, availableQuantity + quantity,
                "주문 예약 해제", "RELEASE", null, orderId);

        log.info("예약 재고 해제: 상품 ID={}, 이전 예약 재고={}, 해제 수량={}, 새 예약 재고={}, 주문 ID={}",
                productId, oldReservedQuantity, quantity, newReservedQuantity, orderId);

        return true;
    }

    @Override
    public boolean confirmReservedInventory(Long productId, int quantity, Long orderId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("확정할 수량은 0보다 커야 합니다.");
        }

        Inventory inventory = getOrCreateInventory(productId);

        if (inventory.getReservedQuantity() < quantity) {
            log.warn("예약 재고 확정 실패: 상품 ID={}, 예약 재고={}, 요청 수량={}",
                    productId, inventory.getReservedQuantity(), quantity);
            return false;
        }

        int oldQuantity = inventory.getQuantity();
        int oldReservedQuantity = inventory.getReservedQuantity();

        int newQuantity = oldQuantity - quantity;
        int newReservedQuantity = oldReservedQuantity - quantity;

        inventory.setQuantity(newQuantity);
        inventory.setReservedQuantity(newReservedQuantity);
        inventoryRepository.save(inventory);

        // 재고 변경 이력 저장
        createInventoryHistory(inventory, oldQuantity, newQuantity,
                "주문 확정", "CONFIRM", null, orderId);

        log.info("예약 재고 확정: 상품 ID={}, 이전 재고={}, 이전 예약 재고={}, 확정 수량={}, 새 재고={}, 새 예약 재고={}, 주문 ID={}",
                productId, oldQuantity, oldReservedQuantity, quantity, newQuantity, newReservedQuantity, orderId);

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getLowStockProducts() {
        return inventoryRepository.findLowStockProducts().stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryDTO> getAllInventory(Pageable pageable) {
        return inventoryRepository.findAll(pageable)
                .map(inventoryMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryHistoryDTO> getInventoryHistory(Long productId, Pageable pageable) {
        return inventoryHistoryRepository.findByInventoryProductId(productId, pageable)
                .map(inventoryHistoryMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventoryHistoryDTO> getAllInventoryHistory(Pageable pageable) {
        return inventoryHistoryRepository.findAll(pageable)
                .map(inventoryHistoryMapper::toDTO);
    }

    @Override
    public void updateLowStockThreshold(Long productId, int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("재고 임계값은 0 이상이어야 합니다.");
        }

        Inventory inventory = getOrCreateInventory(productId);
        inventory.setLowStockThreshold(threshold);
        inventoryRepository.save(inventory);

        log.info("재고 임계값 업데이트: 상품 ID={}, 새 임계값={}", productId, threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInStock(Long productId, int quantity) {
        return inventoryRepository.findByProductId(productId)
                .map(inventory -> {
                    int availableQuantity = inventory.getQuantity() - inventory.getReservedQuantity();
                    return availableQuantity >= quantity;
                })
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public int getAvailableQuantity(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(inventory -> inventory.getQuantity() - inventory.getReservedQuantity())
                .orElse(0);
    }

    // 헬퍼 메서드
    private Inventory getOrCreateInventory(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseGet(() -> {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + productId));

                    Inventory newInventory = new Inventory();
                    newInventory.setProduct(product);
                    newInventory.setQuantity(0);
                    newInventory.setReservedQuantity(0);
                    newInventory.setLowStockThreshold(5); // 기본 임계값
                    return inventoryRepository.save(newInventory);
                });
    }

    private void createInventoryHistory(Inventory inventory, int quantityBefore, int quantityAfter,
                                        String reason, String actionType, Long userId, Long orderId) {
        InventoryHistory history = new InventoryHistory();
        history.setInventory(inventory);
        history.setQuantityBefore(quantityBefore);
        history.setQuantityAfter(quantityAfter);
        history.setQuantityChanged(Math.abs(quantityAfter - quantityBefore));
        history.setReason(reason);
        history.setActionType(actionType);
        history.setCreatedAt(LocalDateTime.now());

        if (userId != null) {
            memberRepository.findById(userId).ifPresent(history::setMember);
        }

        history.setOrderId(orderId);

        inventoryHistoryRepository.save(history);
    }

    private InventoryDTO convertToDTO(Inventory inventory) {
        return inventoryMapper.toDTO(inventory);
    }

    private InventoryHistoryDTO convertToHistoryDTO(InventoryHistory history) {
        return inventoryHistoryMapper.toDTO(history);
    }
}