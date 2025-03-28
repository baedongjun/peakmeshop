package com.peakmeshop.service;

import java.util.List;

import com.peakmeshop.dto.CartItemDTO;

public interface CartService {

    /**
     * 장바구니에 상품 추가
     * @param memberId 회원 ID
     * @param productId 상품 ID
     * @param quantity 수량
     * @return 추가된 장바구니 아이템
     */
    CartItemDTO addToCart(Long memberId, Long productId, Integer quantity);

    /**
     * 장바구니 아이템 수량 업데이트
     * @param memberId 회원 ID
     * @param cartItemId 장바구니 아이템 ID
     * @param quantity 변경할 수량
     * @return 업데이트된 장바구니 아이템
     */
    CartItemDTO updateCartItemQuantity(Long memberId, Long cartItemId, Integer quantity);

    /**
     * 장바구니에서 아이템 제거
     * @param memberId 회원 ID
     * @param cartItemId 장바구니 아이템 ID
     */
    void removeFromCart(Long memberId, Long cartItemId);

    /**
     * 회원의 장바구니 아이템 목록 조회
     * @param memberId 회원 ID
     * @return 장바구니 아이템 목록
     */
    List<CartItemDTO> getCartItems(Long memberId);

    /**
     * 장바구니 비우기
     * @param memberId 회원 ID
     */
    void clearCart(Long memberId);

    /**
     * 장바구니 아이템 수 조회
     * @param memberId 회원 ID
     * @return 장바구니 아이템 수
     */
    int getCartItemCount(Long memberId);

    /**
     * 장바구니 총액 계산
     * @param memberId 회원 ID
     * @return 장바구니 총액
     */
    java.math.BigDecimal calculateCartTotal(Long memberId);
}