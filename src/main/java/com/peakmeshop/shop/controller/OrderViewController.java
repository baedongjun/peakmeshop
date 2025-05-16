package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.OrderDTO;
import com.peakmeshop.api.dto.OrderItemDTO;
import com.peakmeshop.domain.enums.OrderStatus;
import com.peakmeshop.domain.service.CartService;
import com.peakmeshop.domain.service.MemberService;
import com.peakmeshop.domain.service.OrderService;
import com.peakmeshop.domain.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 주문, 결제 관련 뷰 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
@Slf4j
public class OrderViewController {

    private final OrderService orderService;
    private final ProductService productService;
    private final MemberService memberService;
    private final CartService cartService;

    /**
     * 장바구니
     */
    @GetMapping("/cart")
    public String cart(Model model, Authentication authentication) {
        // 장바구니 페이지는 클라이언트 측 자바스크립트에서 API를 호출하여 데이터를 가져오므로
        // 여기서는 기본 템플릿만 반환
        return "shop/order/cart";
    }

    /**
     * 장바구니 견적서
     */
    @GetMapping("/cart-estimate")
    public String cartEstimate(Model model, Authentication authentication) {
        // 장바구니 견적서 페이지
        return "shop/order/cart-estimate";
    }

    /**
     * 주문/결제 페이지
     */
    @GetMapping("/checkout")
    public String checkout(Model model, Authentication authentication) {
        // 로그인 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login?redirectUrl=/shop/order/checkout";
        }

        // 체크아웃 페이지를 위한 데이터는 클라이언트 측에서 API 호출을 통해 가져옴
        return "shop/order/checkout";
    }

    /**
     * 주문 상세 페이지
     */
    @GetMapping("/detail/{orderNumber}")
    public String orderDetail(@PathVariable String orderNumber, Model model, Authentication authentication) {
        // 로그인 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login?redirectUrl=/shop/order/detail/" + orderNumber;
        }

        try {
            // 주문 정보 조회
            OrderDTO order = orderService.getOrderByOrderNumber(orderNumber);

            // 주문 항목 조회
            List<OrderItemDTO> orderItems = orderService.getOrderItems(order.getId());

            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderItems);

            return "shop/order/order-detail";
        } catch (Exception e) {
            log.error("주문 상세 조회 실패: {}", e.getMessage());
            model.addAttribute("message", "주문을 찾을 수 없습니다.");
            model.addAttribute("messageType", "danger");
            return "redirect:/mypage/orders";
        }
    }

    /**
     * 주문 완료 페이지
     */
    @GetMapping("/complete")
    public String orderComplete(@RequestParam(required = false) String orderNumber, Model model) {
        if (orderNumber != null && !orderNumber.isEmpty()) {
            try {
                // 주문 정보 조회
                OrderDTO order = orderService.getOrderByOrderNumber(orderNumber);

                // 주문 항목 조회
                List<OrderItemDTO> orderItems = orderService.getOrderItems(order.getId());

                model.addAttribute("order", order);
                model.addAttribute("orderItems", orderItems);
            } catch (Exception e) {
                log.error("주문 완료 페이지 로드 실패: {}", e.getMessage());
                // 오류 발생 시에도 페이지는 보여주되, 기본 데이터만 표시
            }
        }

        return "shop/order/order-complete";
    }

    /**
     * 주문 취소 처리
     */
    @PostMapping("/cancel/{id}")
    public String cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            Model model,
            Authentication authentication) {

        // 로그인 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            // 주문 취소 처리 - OrderService에 있는 updateOrderStatus(Long id, OrderStatus status) 메소드 사용
            OrderDTO cancelledOrder = orderService.updateOrderStatus(id, OrderStatus.CANCELLED);

            model.addAttribute("message", "주문이 성공적으로 취소되었습니다.");
            model.addAttribute("messageType", "success");

            return "redirect:/mypage/orders";
        } catch (Exception e) {
            log.error("주문 취소 실패: {}", e.getMessage());
            model.addAttribute("message", "주문 취소 중 오류가 발생했습니다.");
            model.addAttribute("messageType", "danger");

            return "redirect:/mypage/orders";
        }
    }

    /**
     * 주문 확정 처리
     */
    @PostMapping("/complete/{id}")
    public String completeOrder(
            @PathVariable Long id,
            Model model,
            Authentication authentication) {

        // 로그인 확인
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            // 주문 확정 처리
            OrderDTO completedOrder = orderService.updateOrderStatus(id, OrderStatus.COMPLETED);

            model.addAttribute("message", "주문이 성공적으로 확정되었습니다.");
            model.addAttribute("messageType", "success");

            return "redirect:/mypage/orders";
        } catch (Exception e) {
            log.error("주문 확정 실패: {}", e.getMessage());
            model.addAttribute("message", "주문 확정 중 오류가 발생했습니다.");
            model.addAttribute("messageType", "danger");

            return "redirect:/mypage/orders";
        }
    }
}

