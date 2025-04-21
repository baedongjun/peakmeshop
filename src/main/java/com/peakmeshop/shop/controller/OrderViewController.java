package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.CartDTO;
import com.peakmeshop.api.dto.OrderDTO;
import com.peakmeshop.domain.service.CartService;
import com.peakmeshop.domain.service.OrderService;
import com.peakmeshop.domain.service.PaymentService;
import com.peakmeshop.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * 주문, 결제 관련 뷰 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderViewController {
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ProductService productService;

    /**
     * 장바구니
     */
    @GetMapping("/cart")
    public String cart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            // 장바구니 정보 로드
            CartDTO cart = cartService.getCartByUser(userDetails.getUsername());
            model.addAttribute("cart", cart);

            // 추천 상품 로드
            model.addAttribute("recommendedProducts", 
                cartService.getRecommendedProducts(userDetails.getUsername()));

            return "shop/cart";
        } catch (Exception e) {
            log.error("장바구니 로드 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "장바구니를 로드하는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 장바구니 견적서
     */
    @GetMapping("/cart/estimate")
    public String cartEstimate(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 장바구니 정보 로드
            CartDTO cart = cartService.getCartByUser(userDetails.getUsername());
            if (cart == null || cart.getItems().isEmpty()) {
                return "redirect:/cart?error=empty";
            }
            model.addAttribute("cart", cart);

            return "shop/cart-estimate";
        } catch (Exception e) {
            log.error("견적서 생성 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "견적서를 생성하는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 주문/결제 페이지
     */
    @GetMapping("/checkout")
    public String checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long cartId,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) Map<String, String> options,
            Model model) {
        try {
            // 주문 정보 초기화
            OrderDTO order;
            
            if (cartId != null) {
                // 장바구니에서 주문
                order = orderService.initializeOrderFromCart(cartId);
            } else if (productId != null && quantity != null) {
                // 단일 상품 주문
                order = orderService.initializeOrderFromProduct(productId, quantity, options);
            } else {
                return "redirect:/cart?error=invalid_order";
            }

            if (order == null) {
                return "redirect:/cart?error=invalid_order";
            }

            // 주문 정보 로드
            model.addAttribute("order", order);
            
            // 사용 가능한 쿠폰 로드
            model.addAttribute("availableCoupons", 
                orderService.getAvailableCoupons(userDetails.getUsername(), order.getTotalAmount()));
            
            // 배송지 정보 로드
            model.addAttribute("deliveryAddresses", 
                orderService.getUserDeliveryAddresses(userDetails.getUsername()));
            
            // 결제 수단 정보 로드
            model.addAttribute("paymentMethods", 
                paymentService.getUserPaymentMethods(userDetails.getUsername()));

            return "shop/order/checkout";
        } catch (Exception e) {
            log.error("주문/결제 페이지 로드 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문/결제 페이지를 로드하는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 주문 완료 페이지
     */
    @GetMapping("/order/complete")
    public String orderComplete(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String orderNumber,
            Model model) {
        try {
            // 주문 정보 로드
            OrderDTO order = orderService.getOrderByNumber(orderNumber);
            if (order == null || !order.getUserId().equals(userDetails.getUsername())) {
                return "redirect:/mypage/orders?error=invalid_order";
            }
            model.addAttribute("order", order);

            // 배송 추적 정보 로드
            if (order.getDeliveryTracking() != null) {
                model.addAttribute("trackingInfo", 
                    orderService.getDeliveryTrackingInfo(order.getDeliveryTracking()));
            }

            return "shop/order/complete";
        } catch (Exception e) {
            log.error("주문 완료 정보 로드 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 완료 정보를 로드하는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 주문 상세 페이지
     */
    @GetMapping("/order/{id}")
    public String orderDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            Model model) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            if (order == null || !order.getUserId().equals(userDetails.getUsername())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");
            }
            model.addAttribute("order", order);
            return "shop/order/detail";
        } catch (Exception e) {
            log.error("주문 상세 정보 로드 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 상세 정보를 로드하는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 주문 취소 페이지
     */
    @GetMapping("/mypage/orders/{orderId}/cancel")
    public String cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 주문 정보 로드
            OrderDTO order = orderService.getOrderById(orderId);
            if (order == null || !order.getUserId().equals(userDetails.getUsername())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");
            }

            // 취소 가능 여부 확인
            if (!orderService.isCancellable(orderId)) {
                return "redirect:/mypage/orders?error=cannot_cancel";
            }

            model.addAttribute("order", order);
            return "shop/order/cancel";
        } catch (Exception e) {
            log.error("주문 취소 페이지 로드 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "주문 취소 페이지를 로드하는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 환불 신청 페이지
     */
    @GetMapping("/mypage/orders/{orderId}/refund")
    public String refundOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 주문 정보 로드
            OrderDTO order = orderService.getOrderById(orderId);
            if (order == null || !order.getUserId().equals(userDetails.getUsername())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");
            }

            // 환불 가능 여부 확인
            if (!orderService.isRefundable(orderId)) {
                return "redirect:/mypage/orders?error=cannot_refund";
            }

            model.addAttribute("order", order);
            model.addAttribute("refundReasons", orderService.getRefundReasons());
            return "shop/order/refund";
        } catch (Exception e) {
            log.error("환불 신청 페이지 로드 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "환불 신청 페이지를 로드하는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 반품 신청 페이지
     */
    @GetMapping("/mypage/orders/{orderId}/return")
    public String returnOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 주문 정보 로드
            OrderDTO order = orderService.getOrderById(orderId);
            if (order == null || !order.getUserId().equals(userDetails.getUsername())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");
            }

            // 반품 가능 여부 확인
            if (!orderService.isReturnable(orderId)) {
                return "redirect:/mypage/orders?error=cannot_return";
            }

            model.addAttribute("order", order);
            model.addAttribute("returnReasons", orderService.getReturnReasons());
            return "shop/order/return";
        } catch (Exception e) {
            log.error("반품 신청 페이지 로드 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "반품 신청 페이지를 로드하는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 교환 신청 페이지
     */
    @GetMapping("/mypage/orders/{orderId}/exchange")
    public String exchangeOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 주문 정보 로드
            OrderDTO order = orderService.getOrderById(orderId);
            if (order == null || !order.getUserId().equals(userDetails.getUsername())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");
            }

            // 교환 가능 여부 확인
            if (!orderService.isExchangeable(orderId)) {
                return "redirect:/mypage/orders?error=cannot_exchange";
            }

            model.addAttribute("order", order);
            model.addAttribute("exchangeReasons", orderService.getExchangeReasons());
            return "shop/order/exchange";
        } catch (Exception e) {
            log.error("교환 신청 페이지 로드 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "교환 신청 페이지를 로드하는 중 오류가 발생했습니다.");
        }
    }
}

