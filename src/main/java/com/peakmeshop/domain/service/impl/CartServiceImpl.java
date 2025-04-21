package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.common.exception.BadRequestException;
import com.peakmeshop.domain.entity.*;
import com.peakmeshop.domain.repository.*;
import com.peakmeshop.domain.service.CartService;
import com.peakmeshop.domain.service.MemberService;
import com.peakmeshop.domain.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemOptionRepository cartItemOptionRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductOptionRepository productOptionRepository;
    private final CouponRepository couponRepository;
    private final ProductService productService;
    private final MemberService memberService;

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByUser(String userId) {
        Long memberId = memberService.getMemberIdByUserId(userId);
        return cartRepository.findByMemberId(memberId)
                .map(this::convertToDTO)
                .orElseGet(() -> createNewCart(memberId));
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(String userId, CartItemDTO cartItemDTO) {
        Long memberId = memberService.getMemberIdByUserId(userId);
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> createEmptyCart(memberId));
        
        Product product = productService.getProductEntity(cartItemDTO.getProductId());
        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(cartItemDTO.getQuantity())
                .options(cartItemDTO.getOptions())
                .build();
        
        cart.addItem(cartItem);
        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(String userId, Long cartItemId, CartItemDTO cartItemDTO) {
        Long memberId = memberService.getMemberIdByUserId(userId);
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
                
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        item.updateQuantity(cartItemDTO.getQuantity());
        item.updateOptions(cartItemDTO.getOptions());
        
        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }

    @Override
    @Transactional
    public void removeItemFromCart(String userId, Long cartItemId) {
        Long memberId = memberService.getMemberIdByUserId(userId);
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.removeItem(cartItemId);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(String userId) {
        Long memberId = memberService.getMemberIdByUserId(userId);
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.clearItems();
        cartRepository.save(cart);
    }

    private Cart createEmptyCart(Long memberId) {
        Cart cart = Cart.builder()
                .member(memberService.getMemberById(memberId))
                .build();
        return cartRepository.save(cart);
    }

    private CartDTO createNewCart(Long memberId) {
        Cart cart = createEmptyCart(memberId);
        return convertToDTO(cart);
    }

    private CartDTO convertToDTO(Cart cart) {
        return CartDTO.builder()
                .id(cart.getId())
                .userId(cart.getMember().getUserId())
                .items(cart.getItems().stream()
                        .map(this::convertToCartItemDTO)
                        .collect(Collectors.toList()))
                .totalAmount(calculateTotalAmount(cart))
                .build();
    }

    private CartItemDTO convertToCartItemDTO(CartItem item) {
        return CartItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getProduct().getPrice())
                .options(item.getOptions())
                .build();
    }

    private BigDecimal calculateTotalAmount(Cart cart) {
        return cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByMemberId(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return convertToDTO(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByGuestId(String guestId) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO getOrCreateCart(Long memberId) {
        Optional<Cart> existingCart = cartRepository.findByMemberId(memberId);

        if (existingCart.isPresent()) {
            return convertToDTO(existingCart.get());
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Cart newCart = Cart.builder()
                .member(member)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Cart savedCart = cartRepository.save(newCart);
        return convertToDTO(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartBySessionId(String sessionId) {
        Cart cart = cartRepository.findByGuestId(sessionId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));
        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO createGuestCart(String guestId) {
        Optional<Cart> existingCart = cartRepository.findByGuestId(guestId);

        if (existingCart.isPresent()) {
            return convertToDTO(existingCart.get());
        }

        Cart newCart = Cart.builder()
                .guestId(guestId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Cart savedCart = cartRepository.save(newCart);
        return convertToDTO(savedCart);
    }

    @Override
    @Transactional
    public CartDTO addItemToGuestCart(String guestId, CartRequestDTO requestDTO) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .guestId(guestId)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return cartRepository.save(newCart);
                });

        return addItemToCart(cart, requestDTO);
    }

    private CartDTO addItemToCart(Cart cart, CartRequestDTO requestDTO) {
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new UsernameNotFoundException("상품을 찾을 수 없습니다."));

        ProductVariant variant = null;
        if (requestDTO.getVariantId() != null) {
            variant = productVariantRepository.findById(requestDTO.getVariantId())
                    .orElseThrow(() -> new UsernameNotFoundException("상품 옵션을 찾을 수 없습니다."));
        }

        // 재고 확인
        if (variant != null) {
            if (variant.getStock() < requestDTO.getQuantity()) {
                throw new BadRequestException("재고가 부족합니다.");
            }
        } else {
            if (product.getStock() < requestDTO.getQuantity()) {
                throw new BadRequestException("재고가 부족합니다.");
            }
        }

        // 동일한 상품이 이미 장바구니에 있는지 확인
        boolean itemExists = false;

        if (variant != null) {
            for (CartItem item : cart.getItems()) {
                if (item.getProduct().getId().equals(product.getId()) &&
                        item.getVariant() != null &&
                        item.getVariant().getId().equals(variant.getId())) {

                    // 옵션이 동일한지 확인
                    if (requestDTO.getOptions() != null && !requestDTO.getOptions().isEmpty()) {
                        List<Long> existingOptionIds = item.getOptions().stream()
                                .map(option -> option.getProductOption().getId())
                                .collect(Collectors.toList());

                        List<Long> newOptionIds = requestDTO.getOptions().stream()
                                .map(CartRequestDTO.CartOptionDTO::getOptionId)
                                .collect(Collectors.toList());

                        if (existingOptionIds.size() == newOptionIds.size() && existingOptionIds.containsAll(newOptionIds)) {
                            // 동일한 상품, 동일한 옵션이므로 수량만 증가
                            item.setQuantity(item.getQuantity() + requestDTO.getQuantity());
                            itemExists = true;
                            break;
                        }
                    } else if (item.getOptions().isEmpty()) {
                        // 옵션이 없는 경우
                        item.setQuantity(item.getQuantity() + requestDTO.getQuantity());
                        itemExists = true;
                        break;
                    }
                }
            }
        } else {
            for (CartItem item : cart.getItems()) {
                if (item.getProduct().getId().equals(product.getId()) && item.getVariant() == null) {
                    // 옵션이 동일한지 확인
                    if (requestDTO.getOptions() != null && !requestDTO.getOptions().isEmpty()) {
                        List<Long> existingOptionIds = item.getOptions().stream()
                                .map(option -> option.getProductOption().getId())
                                .collect(Collectors.toList());

                        List<Long> newOptionIds = requestDTO.getOptions().stream()
                                .map(CartRequestDTO.CartOptionDTO::getOptionId)
                                .collect(Collectors.toList());

                        if (existingOptionIds.size() == newOptionIds.size() && existingOptionIds.containsAll(newOptionIds)) {
                            // 동일한 상품, 동일한 옵션이므로 수량만 증가
                            item.setQuantity(item.getQuantity() + requestDTO.getQuantity());
                            itemExists = true;
                            break;
                        }
                    } else if (item.getOptions().isEmpty()) {
                        // 옵션이 없는 경우
                        item.setQuantity(item.getQuantity() + requestDTO.getQuantity());
                        itemExists = true;
                        break;
                    }
                }
            }
        }

        if (!itemExists) {
            // 새 상품 추가
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantity(requestDTO.getQuantity())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            cartItem = cartItemRepository.save(cartItem);

            // 옵션 추가
            if (requestDTO.getOptions() != null && !requestDTO.getOptions().isEmpty()) {
                for (CartRequestDTO.CartOptionDTO optionDTO : requestDTO.getOptions()) {
                    ProductOption productOption = productOptionRepository.findById(optionDTO.getOptionId())
                            .orElseThrow(() -> new UsernameNotFoundException("상품 옵션을 찾을 수 없습니다."));

                    CartItemOption cartItemOption = CartItemOption.builder()
                            .cartItem(cartItem)
                            .productOption(productOption)
                            .value(optionDTO.getValue())
                            .build();

                    cartItemOptionRepository.save(cartItemOption);
                }
            }

            cart.getItems().add(cartItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        return convertToDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long memberId, CartUpdateDTO updateDTO) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));

        return updateCartItem(cart, updateDTO);
    }

    @Override
    @Transactional
    public CartDTO updateGuestCartItem(String guestId, CartUpdateDTO updateDTO) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));

        return updateCartItem(cart, updateDTO);
    }

    private CartDTO updateCartItem(Cart cart, CartUpdateDTO updateDTO) {
        CartItem cartItem = cartItemRepository.findById(updateDTO.getCartItemId())
                .orElseThrow(() -> new UsernameNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("해당 장바구니에 상품이 존재하지 않습니다.");
        }

        // 재고 확인
        if (cartItem.getVariant() != null) {
            if (cartItem.getVariant().getStock() < updateDTO.getQuantity()) {
                throw new BadRequestException("재고가 부족합니다.");
            }
        } else {
            if (cartItem.getProduct().getStock() < updateDTO.getQuantity()) {
                throw new BadRequestException("재고가 부족합니다.");
            }
        }

        cartItem.setQuantity(updateDTO.getQuantity());
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        return convertToDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO removeItemFromCart(Long memberId, Long cartItemId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));

        return removeItemFromCart(cart, cartItemId);
    }

    @Override
    @Transactional
    public CartDTO removeItemFromGuestCart(String guestId, Long cartItemId) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));

        return removeItemFromCart(cart, cartItemId);
    }

    private CartDTO removeItemFromCart(Cart cart, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("해당 장바구니에 상품이 존재하지 않습니다.");
        }

        // 옵션 삭제
        cartItemOptionRepository.deleteAll(cartItem.getOptions());

        // 상품 삭제
        cartItemRepository.delete(cartItem);
        cart.getItems().remove(cartItem);

        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        return convertToDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO clearCart(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));

        return clearCart(cart);
    }

    @Override
    @Transactional
    public CartDTO clearGuestCart(String guestId) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));

        return clearCart(cart);
    }

    private CartDTO clearCart(Cart cart) {
        // 모든 옵션 삭제
        for (CartItem item : cart.getItems()) {
            cartItemOptionRepository.deleteAll(item.getOptions());
        }

        // 모든 상품 삭제
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();

        // 쿠폰 제거
        cart.setCoupon(null);

        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        return convertToDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO applyCoupon(Long memberId, String couponCode) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));

        return applyCoupon(cart, couponCode);
    }

    @Override
    @Transactional
    public CartDTO applyGuestCoupon(String guestId, String couponCode) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));

        return applyCoupon(cart, couponCode);
    }

    private CartDTO applyCoupon(Cart cart, String couponCode) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new UsernameNotFoundException("쿠폰을 찾을 수 없습니다."));

        // 쿠폰 유효성 검사
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getEndDate() != null && coupon.getEndDate().isBefore(now)) {
            throw new BadRequestException("만료된 쿠폰입니다.");
        }

        if (coupon.getStartDate() != null && coupon.getStartDate().isAfter(now)) {
            throw new BadRequestException("아직 사용할 수 없는 쿠폰입니다.");
        }

        if (!Coupon.STATUS_ACTIVE.equals(coupon.getStatus())) {
            throw new BadRequestException("사용할 수 없는 쿠폰입니다.");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new BadRequestException("사용 가능한 쿠폰 수량이 모두 소진되었습니다.");
        }

        // 최소 주문 금액 확인
        BigDecimal subtotal = calculateSubtotal(cart);
        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new BadRequestException("최소 주문 금액을 충족하지 않습니다. 최소 주문 금액: " + coupon.getMinOrderAmount() + "원");
        }

        cart.setCoupon(coupon);
        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        return convertToDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO removeCoupon(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));

        return removeCoupon(cart);
    }

    @Override
    @Transactional
    public CartDTO removeGuestCoupon(String guestId) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new UsernameNotFoundException("장바구니를 찾을 수 없습니다."));

        return removeCoupon(cart);
    }

    private CartDTO removeCoupon(Cart cart) {
        cart.setCoupon(null);
        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        return convertToDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO mergeGuestCartWithMemberCart(String guestId, Long memberId) {
        Cart guestCart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new UsernameNotFoundException("게스트 장바구니를 찾을 수 없습니다."));

        Cart memberCart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> {
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));

                    Cart newCart = Cart.builder()
                            .member(member)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return cartRepository.save(newCart);
                });

        // 게스트 장바구니의 상품을 회원 장바구니로 이동
        for (CartItem guestItem : guestCart.getItems()) {
            boolean itemExists = false;

            // 동일한 상품이 이미 회원 장바구니에 있는지 확인
            for (CartItem memberItem : memberCart.getItems()) {
                if (guestItem.getProduct().getId().equals(memberItem.getProduct().getId()) &&
                        ((guestItem.getVariant() == null && memberItem.getVariant() == null) ||
                                (guestItem.getVariant() != null && memberItem.getVariant() != null &&
                                        guestItem.getVariant().getId().equals(memberItem.getVariant().getId())))) {

                    // 옵션이 동일한지 확인
                    List<Long> guestOptionIds = guestItem.getOptions().stream()
                            .map(option -> option.getProductOption().getId())
                            .collect(Collectors.toList());

                    List<Long> memberOptionIds = memberItem.getOptions().stream()
                            .map(option -> option.getProductOption().getId())
                            .collect(Collectors.toList());

                    if (guestOptionIds.size() == memberOptionIds.size() && guestOptionIds.containsAll(memberOptionIds)) {
                        // 동일한 상품, 동일한 옵션이므로 수량만 증가
                        memberItem.setQuantity(memberItem.getQuantity() + guestItem.getQuantity());
                        memberItem.setUpdatedAt(LocalDateTime.now());
                        cartItemRepository.save(memberItem);
                        itemExists = true;
                        break;
                    }
                }
            }

            if (!itemExists) {
                // 새 상품 추가
                CartItem newItem = CartItem.builder()
                        .cart(memberCart)
                        .product(guestItem.getProduct())
                        .variant(guestItem.getVariant())
                        .quantity(guestItem.getQuantity())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                newItem = cartItemRepository.save(newItem);

                // 옵션 복사
                for (CartItemOption guestOption : guestItem.getOptions()) {
                    CartItemOption newOption = CartItemOption.builder()
                            .cartItem(newItem)
                            .productOption(guestOption.getProductOption())
                            .value(guestOption.getValue())
                            .build();

                    cartItemOptionRepository.save(newOption);
                }

                memberCart.getItems().add(newItem);
            }
        }

        // 게스트 장바구니의 쿠폰이 있고 회원 장바구니에 쿠폰이 없는 경우 쿠폰 이동
        if (guestCart.getCoupon() != null && memberCart.getCoupon() == null) {
            memberCart.setCoupon(guestCart.getCoupon());
        }

        memberCart.setUpdatedAt(LocalDateTime.now());
        Cart updatedMemberCart = cartRepository.save(memberCart);

        // 게스트 장바구니 삭제
        clearCart(guestCart);
        cartRepository.delete(guestCart);

        return convertToDTO(updatedMemberCart);
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            BigDecimal itemPrice;

            if (item.getVariant() != null && item.getVariant().getPrice() != null) {
                itemPrice = item.getVariant().getPrice();
            } else {
                itemPrice = item.getProduct().getPrice();
            }

            // 옵션 가격 추가
            for (CartItemOption option : item.getOptions()) {
                if (option.getProductOption().getAdditionalPrice() != null) {
                    itemPrice = itemPrice.add(option.getProductOption().getAdditionalPrice());
                }
            }

            BigDecimal itemTotal = itemPrice.multiply(new BigDecimal(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }

        return subtotal;
    }

    private BigDecimal calculateDiscount(Cart cart) {
        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal discount = BigDecimal.ZERO;

        if (cart.getCoupon() != null) {
            Coupon coupon = cart.getCoupon();

            // 최소 주문 금액 확인
            if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
                return BigDecimal.ZERO;
            }

            // 할인 계산
            if (Coupon.DISCOUNT_TYPE_PERCENTAGE.equals(coupon.getDiscountType())) {
                BigDecimal discountValue = coupon.getDiscountValue();
                if (discountValue != null) {
                    // Integer를 BigDecimal로 변환
                    BigDecimal discountPercent = discountValue;
                    // RoundingMode를 명시적으로 지정
                    BigDecimal hundred = new BigDecimal(100);
                    discount = subtotal.multiply(discountPercent).divide(hundred, 2, BigDecimal.ROUND_HALF_UP);

                    // 최대 할인 금액 제한
                    if (coupon.getMaxDiscountAmount() != null) {
                        BigDecimal maxDiscount = coupon.getMaxDiscountAmount();
                        if (discount.compareTo(maxDiscount) > 0) {
                            discount = maxDiscount;
                        }
                    }
                }
            } else if (Coupon.DISCOUNT_TYPE_FIXED.equals(coupon.getDiscountType())) {
                if (coupon.getDiscountValue() != null) {
                    // Integer를 BigDecimal로 변환
                    discount = coupon.getDiscountValue();

                    // 할인 금액이 장바구니 합계보다 크면 장바구니 합계로 제한
                    if (discount.compareTo(subtotal) > 0) {
                        discount = subtotal;
                    }
                }
            }
        }

        return discount;
    }
}