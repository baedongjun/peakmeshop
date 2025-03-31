package com.peakmeshop.api.controller;

import com.peakmeshop.api.dto.WishlistDTO;
import com.peakmeshop.api.dto.WishlistItemDTO;
import com.peakmeshop.common.security.oauth2.user.UserPrincipal;
import com.peakmeshop.domain.service.WishlistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@PreAuthorize("isAuthenticated()")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<WishlistDTO> getWishlist(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(wishlistService.getOrCreateWishlist(userPrincipal.getId()));
    }

    @GetMapping("/items")
    public ResponseEntity<Page<WishlistItemDTO>> getWishlistItems(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(wishlistService.getWishlistItems(userPrincipal.getId(), pageable));
    }

    @PostMapping("/items/{productId}")
    public ResponseEntity<WishlistDTO> addItemToWishlist(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long productId) {
        return new ResponseEntity<>(
                wishlistService.addItemToWishlist(userPrincipal.getId(), productId),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeItemFromWishlist(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long productId) {
        if (wishlistService.removeItemFromWishlist(userPrincipal.getId(), productId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearWishlist(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        wishlistService.clearWishlist(userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isProductInWishlist(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam Long productId) {
        return ResponseEntity.ok(wishlistService.isProductInWishlist(userPrincipal.getId(), productId));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getWishlistItemCount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(wishlistService.getWishlistItemCount(userPrincipal.getId()));
    }
}