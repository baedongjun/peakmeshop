package com.peakmeshop.api.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.api.dto.CartDTO;
import com.peakmeshop.api.dto.CartRequestDTO;
import com.peakmeshop.api.dto.CartUpdateDTO;
import com.peakmeshop.domain.service.CartService;
import com.peakmeshop.domain.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MemberService memberService;

    private static final String GUEST_CART_COOKIE_NAME = "guest_cart_id";
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30일

    @GetMapping
    public ResponseEntity<CartDTO> getCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @CookieValue(name = GUEST_CART_COOKIE_NAME, required = false) String guestCartId,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 로그인한 사용자
        if (userDetails != null) {
            // 게스트 장바구니가 있으면 병합
            if (guestCartId != null && !guestCartId.isEmpty()) {
                CartDTO mergedCart = cartService.mergeGuestCartWithMemberCart(
                        guestCartId,
                        memberService.getMemberByUserId(userDetails.getUsername()).getId());

                // 게스트 장바구니 쿠키 삭제
                Cookie cookie = new Cookie(GUEST_CART_COOKIE_NAME, null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);

                return ResponseEntity.ok(mergedCart);
            }

            // 회원 장바구니 조회 또는 생성
            CartDTO cart = cartService.getOrCreateCart(
                    memberService.getMemberByUserId(userDetails.getUsername()).getId());
            return ResponseEntity.ok(cart);
        }

        // 비로그인 사용자
        if (guestCartId == null || guestCartId.isEmpty()) {
            // 게스트 장바구니 ID 생성
            guestCartId = UUID.randomUUID().toString();

            // 쿠키 설정
            Cookie cookie = new Cookie(GUEST_CART_COOKIE_NAME, guestCartId);
            cookie.setMaxAge(COOKIE_MAX_AGE);
            cookie.setPath("/");
            response.addCookie(cookie);

            // 게스트 장바구니 생성
            CartDTO cart = cartService.createGuestCart(guestCartId);
            return ResponseEntity.ok(cart);
        }

        // 기존 게스트 장바구니 조회
        CartDTO cart = cartService.getCartBySessionId(guestCartId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @CookieValue(name = GUEST_CART_COOKIE_NAME, required = false) String guestCartId,
            @RequestBody CartRequestDTO requestDTO,
            HttpServletResponse response) {

        // 로그인한 사용자
        if (userDetails != null) {
            CartDTO updatedCart = cartService.addItemToCart(
                    memberService.getMemberByUserId(userDetails.getUsername()).getId(),
                    requestDTO);
            return ResponseEntity.ok(updatedCart);
        }

        // 비로그인 사용자
        if (guestCartId == null || guestCartId.isEmpty()) {
            // 게스트 장바구니 ID 생성
            guestCartId = UUID.randomUUID().toString();

            // 쿠키 설정
            Cookie cookie = new Cookie(GUEST_CART_COOKIE_NAME, guestCartId);
            cookie.setMaxAge(COOKIE_MAX_AGE);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        CartDTO updatedCart = cartService.addItemToGuestCart(guestCartId, requestDTO);
        return ResponseEntity.ok(updatedCart);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @CookieValue(name = GUEST_CART_COOKIE_NAME, required = false) String guestCartId,
            @PathVariable Long itemId,
            @RequestBody CartUpdateDTO updateDTO) {

        // 아이템 ID 설정
        updateDTO.setCartItemId(itemId);

        // 로그인한 사용자
        if (userDetails != null) {
            CartDTO updatedCart = cartService.updateCartItem(
                    memberService.getMemberByUserId(userDetails.getUsername()).getId(),
                    updateDTO);
            return ResponseEntity.ok(updatedCart);
        }

        // 비로그인 사용자
        if (guestCartId == null || guestCartId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CartDTO updatedCart = cartService.updateGuestCartItem(guestCartId, updateDTO);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> removeCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @CookieValue(name = GUEST_CART_COOKIE_NAME, required = false) String guestCartId,
            @PathVariable Long itemId) {

        // 로그인한 사용자
        if (userDetails != null) {
            CartDTO updatedCart = cartService.removeItemFromCart(
                    memberService.getMemberByUserId(userDetails.getUsername()).getId(),
                    itemId);
            return ResponseEntity.ok(updatedCart);
        }

        // 비로그인 사용자
        if (guestCartId == null || guestCartId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CartDTO updatedCart = cartService.removeItemFromGuestCart(guestCartId, itemId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping
    public ResponseEntity<CartDTO> clearCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @CookieValue(name = GUEST_CART_COOKIE_NAME, required = false) String guestCartId) {

        // 로그인한 사용자
        if (userDetails != null) {
            CartDTO clearedCart = cartService.clearCart(
                    memberService.getMemberByUserId(userDetails.getUsername()).getId());
            return ResponseEntity.ok(clearedCart);
        }

        // 비로그인 사용자
        if (guestCartId == null || guestCartId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CartDTO clearedCart = cartService.clearGuestCart(guestCartId);
        return ResponseEntity.ok(clearedCart);
    }

    @PostMapping("/coupon/{couponCode}")
    public ResponseEntity<CartDTO> applyCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @CookieValue(name = GUEST_CART_COOKIE_NAME, required = false) String guestCartId,
            @PathVariable String couponCode) {

        // 로그인한 사용자
        if (userDetails != null) {
            CartDTO updatedCart = cartService.applyCoupon(
                    memberService.getMemberByUserId(userDetails.getUsername()).getId(),
                    couponCode);
            return ResponseEntity.ok(updatedCart);
        }

        // 비로그인 사용자
        if (guestCartId == null || guestCartId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CartDTO updatedCart = cartService.applyGuestCoupon(guestCartId, couponCode);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/coupon")
    public ResponseEntity<CartDTO> removeCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @CookieValue(name = GUEST_CART_COOKIE_NAME, required = false) String guestCartId) {

        // 로그인한 사용자
        if (userDetails != null) {
            CartDTO updatedCart = cartService.removeCoupon(
                    memberService.getMemberByUserId(userDetails.getUsername()).getId());
            return ResponseEntity.ok(updatedCart);
        }

        // 비로그인 사용자
        if (guestCartId == null || guestCartId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CartDTO updatedCart = cartService.removeGuestCoupon(guestCartId);
        return ResponseEntity.ok(updatedCart);
    }
}