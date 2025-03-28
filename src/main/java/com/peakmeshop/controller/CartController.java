package com.peakmeshop.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peakmeshop.dto.CartItemDTO;
import com.peakmeshop.dto.MemberDTO;
import com.peakmeshop.service.CartService;
import com.peakmeshop.service.MemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final MemberService memberService;

    public CartController(CartService cartService, MemberService memberService) {
        this.cartService = cartService;
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCartItems(Principal principal) {
        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        List<CartItemDTO> cartItems = cartService.getCartItems(member.id());
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemDTO> addToCart(
            @Valid @RequestBody Map<String, Object> request,
            Principal principal) {

        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());

        CartItemDTO cartItem = cartService.addToCart(member.id(), productId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CartItemDTO> updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, Integer> request,
            Principal principal) {

        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        Integer quantity = request.get("quantity");

        CartItemDTO cartItem = cartService.updateCartItemQuantity(member.id(), id, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long id,
            Principal principal) {

        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        cartService.removeFromCart(member.id(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(Principal principal) {
        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        cartService.clearCart(member.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartItemCount(Principal principal) {
        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        int count = cartService.getCartItemCount(member.id());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, java.math.BigDecimal>> getCartTotal(Principal principal) {
        MemberDTO member = memberService.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("로그인 정보를 찾을 수 없습니다."));

        java.math.BigDecimal total = cartService.calculateCartTotal(member.id());
        return ResponseEntity.ok(Map.of("total", total));
    }
}