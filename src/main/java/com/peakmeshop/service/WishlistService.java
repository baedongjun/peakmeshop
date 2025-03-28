package com.peakmeshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.dto.WishlistDTO;

public interface WishlistService {

    /**
     * 위시리스트에 상품 추가
     * @param memberId 회원 ID
     * @param productId 상품 ID
     * @return 추가된 위시리스트 아이템
     */
    WishlistDTO addToWishlist(Long memberId, Long productId);

    /**
     * 위시리스트에서 상품 제거
     * @param memberId 회원 ID
     * @param wishlistId 위시리스트 ID
     */
    void removeFromWishlist(Long memberId, Long wishlistId);

    /**
     * 위시리스트에서 상품 ID로 제거
     * @param memberId 회원 ID
     * @param productId 상품 ID
     */
    void removeFromWishlistByProductId(Long memberId, Long productId);

    /**
     * 회원의 위시리스트 목록 조회
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 위시리스트 목록
     */
    Page<WishlistDTO> getWishlist(Long memberId, Pageable pageable);

    /**
     * 상품이 위시리스트에 있는지 확인
     * @param memberId 회원 ID
     * @param productId 상품 ID
     * @return 위시리스트 포함 여부
     */
    boolean isInWishlist(Long memberId, Long productId);

    /**
     * 위시리스트 아이템 수 조회
     * @param memberId 회원 ID
     * @return 위시리스트 아이템 수
     */
    int getWishlistCount(Long memberId);
}