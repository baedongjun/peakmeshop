package com.peakmeshop.domain.service.impl;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.api.mapper.*;
import com.peakmeshop.common.exception.BadRequestException;
import com.peakmeshop.domain.entity.*;
import com.peakmeshop.domain.repository.*;
import com.peakmeshop.domain.service.CartService;
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
    private final ProductOptionRepository productOptionRepository;
    private final CouponRepository couponRepository;

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final MemberMapper memberMapper;
    private final ProductMapper productMapper;
    private final ProductOptionMapper productOptionMapper;

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByMemberId(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return cartMapper.toDTO(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByGuestId(String guestId) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return cartMapper.toDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO getOrCreateCart(Long memberId) {
        Optional<Cart> existingCart = cartRepository.findByMemberId(memberId);

        if (existingCart.isPresent()) {
            return cartMapper.toDTO(existingCart.get());
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Cart newCart = Cart.builder()
                .member(member)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Cart savedCart = cartRepository.save(newCart);
        return cartMapper.toDTO(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartBySessionId(String sessionId) {
        Cart cart = cartRepository.findByGuestId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return cartMapper.toDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO createGuestCart(String guestId) {
        Optional<Cart> existingCart = cartRepository.findByGuestId(guestId);

        if (existingCart.isPresent()) {
            return cartMapper.toDTO(existingCart.get());
        }

        Cart newCart = Cart.builder()
                .guestId(guestId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Cart savedCart = cartRepository.save(newCart);
        return cartMapper.toDTO(savedCart);
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(Long memberId, CartRequestDTO requestDTO) {
        Cart cart = cartRepository.findByMemberId(memberId)
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

        return addItemToCart(cart, requestDTO);
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
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        ProductOption option = null;
        ProductOptionValue optionValue = null;
        if (requestDTO.getOptionId() != null) {
            option = productOptionRepository.findById(requestDTO.getOptionId())
                    .orElseThrow(() -> new EntityNotFoundException("상품 옵션을 찾을 수 없습니다."));
            
            if (requestDTO.getOptionValueId() != null) {
                optionValue = option.getOptionValues().stream()
                        .filter(value -> value.getId().equals(requestDTO.getOptionValueId()))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("옵션 값을 찾을 수 없습니다."));
            }
        }

        // 재고 확인
        if (optionValue != null) {
            if (optionValue.getStock() < requestDTO.getQuantity()) {
                throw new BadRequestException("재고가 부족합니다.");
            }
        } else {
            if (product.getStock() < requestDTO.getQuantity()) {
                throw new BadRequestException("재고가 부족합니다.");
            }
        }

        // 동일한 상품이 이미 장바구니에 있는지 확인
        boolean itemExists = false;

        if (option != null) {
            for (CartItem item : cart.getCartItems()) {
                if (item.getProduct().getId().equals(product.getId()) &&
                        item.getOption() != null &&
                        item.getOption().getId().equals(option.getId()) &&
                        ((optionValue != null && item.getOptionValue() != null && 
                          item.getOptionValue().getId().equals(optionValue.getId())) ||
                         (optionValue == null && item.getOptionValue() == null))) {

                    // 옵션이 동일한지 확인
                    if (requestDTO.getOptions() != null && !requestDTO.getOptions().isEmpty()) {
                        List<Long> existingOptionIds = item.getOptions().stream()
                                .map(options -> options.getProductOption().getId())
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
            for (CartItem item : cart.getCartItems()) {
                if (item.getProduct().getId().equals(product.getId()) && 
                    item.getOption() == null && 
                    item.getOptionValue() == null) {
                    // 옵션이 동일한지 확인
                    if (requestDTO.getOptions() != null && !requestDTO.getOptions().isEmpty()) {
                        List<Long> existingOptionIds = item.getOptions().stream()
                                .map(options -> options.getProductOption().getId())
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
            // 가격 계산
            BigDecimal price = product.getPrice();
            if (optionValue != null && optionValue.getAdditionalPrice() != null) {
                price = price.add(optionValue.getAdditionalPrice());
            }

            // 새 상품 추가
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .option(option)
                    .optionValue(optionValue)
                    .quantity(requestDTO.getQuantity())
                    .price(price)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            cartItem = cartItemRepository.save(cartItem);

            // 옵션 추가
            if (requestDTO.getOptions() != null && !requestDTO.getOptions().isEmpty()) {
                for (CartRequestDTO.CartOptionDTO optionDTO : requestDTO.getOptions()) {
                    ProductOption productOption = productOptionRepository.findById(optionDTO.getOptionId())
                            .orElseThrow(() -> new EntityNotFoundException("상품 옵션을 찾을 수 없습니다."));

                    CartItemOption cartItemOption = CartItemOption.builder()
                            .cartItem(cartItem)
                            .productOption(productOption)
                            .value(optionDTO.getValue())
                            .build();

                    cartItemOptionRepository.save(cartItemOption);
                }
            }

            cart.getCartItems().add(cartItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        Cart updatedCart = cartRepository.save(cart);

        return cartMapper.toDTO(updatedCart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long memberId, CartUpdateDTO updateDTO) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return updateCartItem(cart, updateDTO);
    }

    @Override
    @Transactional
    public CartDTO updateGuestCartItem(String guestId, CartUpdateDTO updateDTO) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return updateCartItem(cart, updateDTO);
    }

    private CartDTO updateCartItem(Cart cart, CartUpdateDTO updateDTO) {
        CartItem cartItem = cartItemRepository.findById(updateDTO.getCartItemId())
                .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("해당 장바구니에 속한 상품이 아닙니다.");
        }

        cartItem.setQuantity(updateDTO.getQuantity());
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);

        return cartMapper.toDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO removeItemFromCart(Long memberId, Long cartItemId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return removeItemFromCart(cart, cartItemId);
    }

    @Override
    @Transactional
    public CartDTO removeItemFromGuestCart(String guestId, Long cartItemId) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return removeItemFromCart(cart, cartItemId);
    }

    private CartDTO removeItemFromCart(Cart cart, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("해당 장바구니에 속한 상품이 아닙니다.");
        }

        cartItemRepository.delete(cartItem);
        cart.getCartItems().remove(cartItem);

        return cartMapper.toDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO clearCart(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return clearCart(cart);
    }

    @Override
    @Transactional
    public CartDTO clearGuestCart(String guestId) {
        Cart cart = cartRepository.findByGuestId(guestId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니를 찾을 수 없습니다."));
        return clearCart(cart);
    }

    private CartDTO clearCart(Cart cart) {
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getCartItems().clear();
        cart.setCoupon(null);
        cart.setUpdatedAt(LocalDateTime.now());
        
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDTO(savedCart);
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

        return cartMapper.toDTO(updatedCart);
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

        return cartMapper.toDTO(updatedCart);
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
        for (CartItem guestItem : guestCart.getCartItems()) {
            boolean itemExists = false;

            // 동일한 상품이 이미 회원 장바구니에 있는지 확인
            for (CartItem memberItem : memberCart.getCartItems()) {
                if (guestItem.getProduct().getId().equals(memberItem.getProduct().getId()) &&
                        ((guestItem.getOption() == null && memberItem.getOption() == null) ||
                                (guestItem.getOption() != null && memberItem.getOption() != null &&
                                        guestItem.getOption().getId().equals(memberItem.getOption().getId())))) {

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
                        .option(guestItem.getOption())
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

                memberCart.getCartItems().add(newItem);
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

        return cartMapper.toDTO(updatedMemberCart);
    }

    private BigDecimal calculateSubtotal(Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDiscount(Cart cart) {
        if (cart.getCoupon() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal subtotal = calculateSubtotal(cart);
        BigDecimal discount = BigDecimal.ZERO;

        switch (cart.getCoupon().getDiscountType()) {
            case Coupon.DISCOUNT_TYPE_PERCENTAGE:
                discount = subtotal.multiply(cart.getCoupon().getDiscountValue().divide(new BigDecimal(100)));
                if (cart.getCoupon().getMaxDiscountAmount() != null) {
                    discount = discount.min(cart.getCoupon().getMaxDiscountAmount());
                }
                break;
            case Coupon.DISCOUNT_TYPE_FIXED:
                discount = cart.getCoupon().getDiscountValue();
                break;
        }

        return discount;
    }
}