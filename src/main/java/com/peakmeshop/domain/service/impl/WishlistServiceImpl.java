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
import com.peakmeshop.api.mapper.WishlistMapper;
import com.peakmeshop.api.mapper.WishlistItemMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;
    private final WishlistItemMapper wishlistItemMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<WishlistDTO> getWishlistByMemberId(Long memberId) {
        Optional<Wishlist> wishlistOpt = wishlistRepository.findByMemberId(memberId);

        if (wishlistOpt.isPresent()) {
            return Optional.of(wishlistMapper.toDto(wishlistOpt.get()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public WishlistDTO getOrCreateWishlist(Long memberId) {
        Wishlist wishlist = wishlistRepository.findByMemberId(memberId)
                .orElseGet(() -> createNewWishlist(memberId));

        return wishlistMapper.toDto(wishlist);
    }

    @Override
    @Transactional
    public WishlistDTO addItemToWishlist(Long memberId, Long productId) {
        // 위시리스트 조회 또는 생성
        Wishlist wishlist = wishlistRepository.findByMemberId(memberId)
                .orElseGet(() -> createNewWishlist(memberId));

        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + productId));

        // 이미 위시리스트에 있는 상품인지 확인
        boolean exists = wishlist.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));

        if (!exists) {
            // 새 상품 추가
            WishlistItem wishlistItem = new WishlistItem();
            wishlistItem.setWishlist(wishlist);
            wishlistItem.setProduct(product);
            // WishlistItem에 createdAt 필드가 없는 경우 이 라인 제거
            // wishlistItem.setCreatedAt(LocalDateTime.now());

            wishlist.getItems().add(wishlistItem);
            wishlistItemRepository.save(wishlistItem);

            // 위시리스트 업데이트
            wishlist.setUpdatedAt(LocalDateTime.now());
            wishlistRepository.save(wishlist);
        }

        return wishlistMapper.toDto(wishlist);
    }

    @Override
    @Transactional
    public boolean removeItemFromWishlist(Long memberId, Long productId) {
        // 위시리스트 조회
        Optional<Wishlist> wishlistOpt = wishlistRepository.findByMemberId(memberId);

        if (wishlistOpt.isEmpty()) {
            return false;
        }

        Wishlist wishlist = wishlistOpt.get();

        // 위시리스트 아이템 조회 및 제거
        WishlistItem itemToRemove = null;
        for (WishlistItem item : wishlist.getItems()) {
            if (item.getProduct().getId().equals(productId)) {
                itemToRemove = item;
                break;
            }
        }

        if (itemToRemove != null) {
            wishlist.getItems().remove(itemToRemove);
            wishlistItemRepository.delete(itemToRemove);

            // 위시리스트 업데이트
            wishlist.setUpdatedAt(LocalDateTime.now());
            wishlistRepository.save(wishlist);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public void clearWishlist(Long memberId) {
        // 위시리스트 조회
        Optional<Wishlist> wishlistOpt = wishlistRepository.findByMemberId(memberId);

        if (wishlistOpt.isEmpty()) {
            return;
        }

        Wishlist wishlist = wishlistOpt.get();

        // 위시리스트 아이템 모두 제거
        wishlistItemRepository.deleteAll(wishlist.getItems());
        wishlist.getItems().clear();

        // 위시리스트 업데이트
        wishlist.setUpdatedAt(LocalDateTime.now());
        wishlistRepository.save(wishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(Long memberId, Long productId) {
        Optional<Wishlist> wishlistOpt = wishlistRepository.findByMemberId(memberId);

        if (wishlistOpt.isPresent()) {
            Wishlist wishlist = wishlistOpt.get();
            return wishlist.getItems().stream()
                    .anyMatch(item -> item.getProduct().getId().equals(productId));
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WishlistItemDTO> getWishlistItems(Long memberId, Pageable pageable) {
        return wishlistItemRepository.findByWishlistMemberId(memberId, pageable)
                .map(wishlistItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public int getWishlistItemCount(Long memberId) {
        Optional<Wishlist> wishlistOpt = wishlistRepository.findByMemberId(memberId);

        if (wishlistOpt.isPresent()) {
            return wishlistOpt.get().getItems().size();
        }

        return 0;
    }

    // 새 위시리스트 생성
    private Wishlist createNewWishlist(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다. ID: " + memberId));

        Wishlist wishlist = new Wishlist();
        wishlist.setMember(member);
        wishlist.setItems(new ArrayList<>());
        wishlist.setCreatedAt(LocalDateTime.now());
        wishlist.setUpdatedAt(LocalDateTime.now());

        return wishlistRepository.save(wishlist);
    }
}