package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.WishlistDTO;
import com.peakmeshop.api.dto.WishlistItemDTO;
import com.peakmeshop.domain.entity.Member;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.Wishlist;
import com.peakmeshop.domain.entity.WishlistItem;
import com.peakmeshop.domain.repository.MemberRepository;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.repository.WishlistItemRepository;
import com.peakmeshop.domain.repository.WishlistRepository;
import com.peakmeshop.domain.service.WishlistService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public WishlistDTO getOrCreateWishlist(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Wishlist wishlist = wishlistRepository.findByMemberId(memberId)
                .orElseGet(() -> {
                    Wishlist newWishlist = Wishlist.builder()
                            .member(member)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return wishlistRepository.save(newWishlist);
                });

        return convertToDTO(wishlist);
    }

    @Override
    public Optional<WishlistDTO> getWishlistByMemberId(Long memberId) {
        return wishlistRepository.findByMemberId(memberId)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public WishlistDTO addItemToWishlist(Long memberId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByMemberId(memberId)
                .orElseGet(() -> getOrCreateWishlist(memberId).toEntity());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        if (!isProductInWishlist(memberId, productId)) {
            WishlistItem wishlistItem = WishlistItem.builder()
                    .wishlist(wishlist)
                    .product(product)
                    .createdAt(LocalDateTime.now())
                    .build();
            wishlistItemRepository.save(wishlistItem);
        }

        return convertToDTO(wishlist);
    }

    @Override
    @Transactional
    public boolean removeItemFromWishlist(Long memberId, Long productId) {
        Optional<WishlistItem> wishlistItem = wishlistItemRepository.findByWishlistMemberIdAndProductId(memberId, productId);
        if (wishlistItem.isPresent()) {
            wishlistItemRepository.delete(wishlistItem.get());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void clearWishlist(Long memberId) {
        wishlistItemRepository.deleteByWishlistMemberId(memberId);
    }

    @Override
    public boolean isProductInWishlist(Long memberId, Long productId) {
        return wishlistItemRepository.existsByWishlistMemberIdAndProductId(memberId, productId);
    }

    @Override
    public Page<WishlistItemDTO> getWishlistItems(Long memberId, Pageable pageable) {
        return wishlistItemRepository.findByWishlistMemberId(memberId, pageable)
                .map(this::convertToItemDTO);
    }

    @Override
    public int getWishlistItemCount(Long memberId) {
        return wishlistItemRepository.countByWishlistMemberId(memberId);
    }

    private WishlistDTO convertToDTO(Wishlist wishlist) {
        return WishlistDTO.builder()
                .id(wishlist.getId())
                .memberId(wishlist.getMember().getId())
                .itemCount(wishlistItemRepository.countByWishlistId(wishlist.getId()))
                .createdAt(wishlist.getCreatedAt())
                .updatedAt(wishlist.getUpdatedAt())
                .build();
    }

    private WishlistItemDTO convertToItemDTO(WishlistItem item) {
        return WishlistItemDTO.builder()
                .id(item.getId())
                .wishlistId(item.getWishlist().getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productPrice(item.getProduct().getPrice())
                .productSalePrice(item.getProduct().getSalePrice())
                .productMainImage(item.getProduct().getMainImage())
                .createdAt(item.getCreatedAt())
                .build();
    }
}