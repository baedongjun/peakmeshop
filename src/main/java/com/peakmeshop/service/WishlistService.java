package com.peakmeshop.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.dto.WishlistDTO;
import com.peakmeshop.dto.WishlistItemDTO;

public interface WishlistService {

    WishlistDTO getOrCreateWishlist(Long memberId);

    Optional<WishlistDTO> getWishlistByMemberId(Long memberId);

    WishlistDTO addItemToWishlist(Long memberId, Long productId);

    boolean removeItemFromWishlist(Long memberId, Long productId);

    void clearWishlist(Long memberId);

    boolean isProductInWishlist(Long memberId, Long productId);

    Page<WishlistItemDTO> getWishlistItems(Long memberId, Pageable pageable);

    int getWishlistItemCount(Long memberId);
}