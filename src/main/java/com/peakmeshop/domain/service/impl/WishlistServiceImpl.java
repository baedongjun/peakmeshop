package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public WishlistServiceImpl(
            WishlistRepository wishlistRepository,
            WishlistItemRepository wishlistItemRepository,
            MemberRepository memberRepository,
            ProductRepository productRepository) {
        this.wishlistRepository = wishlistRepository;
        this.wishlistItemRepository = wishlistItemRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WishlistDTO> getWishlistByMemberId(Long memberId) {
        Optional<Wishlist> wishlistOpt = wishlistRepository.findByMemberId(memberId);

        if (wishlistOpt.isPresent()) {
            return Optional.of(convertToDTO(wishlistOpt.get()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public WishlistDTO getOrCreateWishlist(Long memberId) {
        Wishlist wishlist = wishlistRepository.findByMemberId(memberId)
                .orElseGet(() -> createNewWishlist(memberId));

        return convertToDTO(wishlist);
    }

    @Override
    @Transactional
    public WishlistDTO addItemToWishlist(Long memberId, Long productId) {
        // 위시리스트 조회 또는 생성
        Wishlist wishlist = wishlistRepository.findByMemberId(memberId)
                .orElseGet(() -> createNewWishlist(memberId));

        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new UsernameNotFoundException("상품을 찾을 수 없습니다. ID: " + productId));

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

        return convertToDTO(wishlist);
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
        // WishlistItemRepository에 적절한 메서드 추가 필요
        // 임시 구현 (실제로는 WishlistItemRepository에 적절한 메서드 추가 필요)
        return wishlistItemRepository.findByWishlistMemberId(memberId, pageable)
                .map(this::convertToDTO);
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
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다. ID: " + memberId));

        Wishlist wishlist = new Wishlist();
        wishlist.setMember(member);
        wishlist.setItems(new ArrayList<>());
        wishlist.setCreatedAt(LocalDateTime.now());
        wishlist.setUpdatedAt(LocalDateTime.now());

        return wishlistRepository.save(wishlist);
    }

    // 엔티티를 DTO로 변환
    private WishlistDTO convertToDTO(Wishlist wishlist) {
        List<WishlistItemDTO> itemDTOs = wishlist.getItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // WishlistDTO 생성자 매개변수에 맞게 수정
        return new WishlistDTO(
                wishlist.getId(),
                wishlist.getMember().getId(),
                wishlist.getMember().getName(),
                true, // isPublic - 기본값 설정
                false, // isShared - 기본값 설정
                "", // shareUrl - 기본값 설정
                wishlist.getCreatedAt(),
                wishlist.getUpdatedAt(),
                itemDTOs
        );
    }

    // 위시리스트 아이템 엔티티를 DTO로 변환
    private WishlistItemDTO convertToDTO(WishlistItem wishlistItem) {
        Product product = wishlistItem.getProduct();

        // WishlistItemDTO 생성자 매개변수에 맞게 수정
        return new WishlistItemDTO(
                wishlistItem.getId(),
                wishlistItem.getWishlist().getId(),
                null, // ProductDTO 대신 null 전달
                LocalDateTime.now(),
                "", // note
                1,  // quantity
                false, // isPurchased
                false  // isGift
        );
    }
}