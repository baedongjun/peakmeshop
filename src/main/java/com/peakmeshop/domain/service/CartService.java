package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.CartDTO;
import com.peakmeshop.api.dto.CartItemDTO;

public interface CartService {
    
    CartDTO getCartById(Long cartId);
    
    CartDTO getCartByUser(String userId);
    
    CartDTO addItemToCart(String userId, CartItemDTO cartItemDTO);
    
    CartDTO updateCartItem(String userId, Long cartItemId, CartItemDTO cartItemDTO);
    
    void removeItemFromCart(String userId, Long cartItemId);
    
    void clearCart(String userId);
}