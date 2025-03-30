package com.peakmeshop.service;

import com.peakmeshop.dto.CartDTO;
import com.peakmeshop.dto.CartRequestDTO;
import com.peakmeshop.dto.CartUpdateDTO;

public interface CartService {

    CartDTO getCartByMemberId(Long memberId);

    CartDTO getCartByGuestId(String guestId);

    CartDTO getOrCreateCart(Long memberId);

    CartDTO getCartBySessionId(String sessionId);

    CartDTO createGuestCart(String guestId);

    CartDTO addItemToCart(Long memberId, CartRequestDTO requestDTO);

    CartDTO addItemToGuestCart(String guestId, CartRequestDTO requestDTO);

    CartDTO updateCartItem(Long memberId, CartUpdateDTO updateDTO);

    CartDTO updateGuestCartItem(String guestId, CartUpdateDTO updateDTO);

    CartDTO removeItemFromCart(Long memberId, Long cartItemId);

    CartDTO removeItemFromGuestCart(String guestId, Long cartItemId);

    CartDTO clearCart(Long memberId);

    CartDTO clearGuestCart(String guestId);

    CartDTO applyCoupon(Long memberId, String couponCode);

    CartDTO applyGuestCoupon(String guestId, String couponCode);

    CartDTO removeCoupon(Long memberId);

    CartDTO removeGuestCoupon(String guestId);

    CartDTO mergeGuestCartWithMemberCart(String guestId, Long memberId);
}