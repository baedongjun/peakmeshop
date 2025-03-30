package com.peakmeshop.controller;

import com.peakmeshop.dto.InventoryDTO;
import com.peakmeshop.dto.InventoryHistoryDTO;
import com.peakmeshop.security.oauth2.user.UserPrincipal;
import com.peakmeshop.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryDTO> getInventoryByProductId(@PathVariable Long productId) {
        return inventoryService.getInventoryByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryDTO> updateInventory(
            @PathVariable Long productId,
            @RequestParam int quantity,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(inventoryService.updateInventory(
                productId, quantity, reason, userPrincipal.getId()));
    }

    @PostMapping("/product/{productId}/increase")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryDTO> increaseInventory(
            @PathVariable Long productId,
            @RequestParam int quantity,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(inventoryService.increaseInventory(
                productId, quantity, reason, userPrincipal.getId()));
    }

    @PostMapping("/product/{productId}/decrease")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryDTO> decreaseInventory(
            @PathVariable Long productId,
            @RequestParam int quantity,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(inventoryService.decreaseInventory(
                productId, quantity, reason, userPrincipal.getId()));
    }

    @PostMapping("/product/{productId}/reserve")
    public ResponseEntity<Boolean> reserveInventory(
            @PathVariable Long productId,
            @RequestParam int quantity,
            @RequestParam Long orderId) {
        return ResponseEntity.ok(inventoryService.reserveInventory(productId, quantity, orderId));
    }

    @PostMapping("/product/{productId}/release")
    public ResponseEntity<Boolean> releaseReservedInventory(
            @PathVariable Long productId,
            @RequestParam int quantity,
            @RequestParam Long orderId) {
        return ResponseEntity.ok(inventoryService.releaseReservedInventory(productId, quantity, orderId));
    }

    @PostMapping("/product/{productId}/confirm")
    public ResponseEntity<Boolean> confirmReservedInventory(
            @PathVariable Long productId,
            @RequestParam int quantity,
            @RequestParam Long orderId) {
        return ResponseEntity.ok(inventoryService.confirmReservedInventory(productId, quantity, orderId));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InventoryDTO>> getLowStockProducts() {
        return ResponseEntity.ok(inventoryService.getLowStockProducts());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InventoryDTO>> getAllInventory(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getAllInventory(pageable));
    }

    @GetMapping("/history/product/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InventoryHistoryDTO>> getInventoryHistory(
            @PathVariable Long productId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getInventoryHistory(productId, pageable));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InventoryHistoryDTO>> getAllInventoryHistory(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getAllInventoryHistory(pageable));
    }

    @PutMapping("/product/{productId}/threshold")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateLowStockThreshold(
            @PathVariable Long productId,
            @RequestParam int threshold) {
        inventoryService.updateLowStockThreshold(productId, threshold);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/product/{productId}/in-stock")
    public ResponseEntity<Boolean> isProductInStock(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(inventoryService.isProductInStock(productId, quantity));
    }

    @GetMapping("/product/{productId}/available")
    public ResponseEntity<Integer> getAvailableQuantity(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getAvailableQuantity(productId));
    }
}