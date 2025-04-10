package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.PromotionDTO;
import com.peakmeshop.common.exception.ResourceNotFoundException;
import com.peakmeshop.domain.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.net.URI;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    public ResponseEntity<PromotionDTO> createPromotion(@Valid @RequestBody PromotionDTO promotionDTO) {
        PromotionDTO created = promotionService.createPromotion(promotionDTO);
        return ResponseEntity.created(URI.create("/api/promotions/" + created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromotionDTO> updatePromotion(
            @PathVariable Long id,
            @Valid @RequestBody PromotionDTO promotionDTO
    ) {
        return promotionService.getPromotion(id)
                .map(existing -> {
                    promotionDTO.setId(id);
                    PromotionDTO updated = promotionService.updatePromotion(id, promotionDTO);
                    return ResponseEntity.ok(updated);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        return promotionService.getPromotion(id)
                .map(promotion -> {
                    promotionService.deletePromotion(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startPromotion(@PathVariable Long id) {
        return promotionService.getPromotion(id)
                .map(promotion -> {
                    promotionService.startPromotion(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<Void> endPromotion(@PathVariable Long id) {
        return promotionService.getPromotion(id)
                .map(promotion -> {
                    promotionService.endPromotion(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendPromotion(@PathVariable Long id) {
        return promotionService.getPromotion(id)
                .map(promotion -> {
                    promotionService.suspendPromotion(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<Void> resumePromotion(@PathVariable Long id) {
        return promotionService.getPromotion(id)
                .map(promotion -> {
                    promotionService.resumePromotion(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Promotion", "id", id));
    }
} 