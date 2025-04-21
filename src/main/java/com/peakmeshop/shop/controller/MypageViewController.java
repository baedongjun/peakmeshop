package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.domain.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 마이페이지 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageViewController {
    private final MemberService memberService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final PointService pointService;
    private final CouponService couponService;
    private final AddressService addressService;
    private final InquiryService inquiryService;
    private final ProductService productService;

    /**
     * 마이페이지 메인
     */
    @GetMapping
    public String myPage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            String userId = userDetails.getUsername();

            // 회원 정보 로드
            MemberDTO member = memberService.getMemberByUserId(userId);
            model.addAttribute("member", member);

            // 주문 현황 로드
            Map<String, Long> orderStatus = orderService.getOrderStatusCount(userId);
            model.addAttribute("orderStatus", orderStatus);

            // 최근 주문 내역 로드
            Page<OrderDTO> recentOrders = orderService.getOrdersByUserId(
                userId, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderDate"))
            );
            model.addAttribute("recentOrders", recentOrders.getContent());

            // 포인트 정보 로드
            PointDTO points = pointService.getPointsByUserId(userId);
            model.addAttribute("points", points.getCurrentPoints());
            model.addAttribute("expiringPoints", points.getExpiringPoints());

            // 쿠폰 정보 로드
            List<CouponDTO> coupons = couponService.getCouponsByUserId(userId);
            model.addAttribute("coupons", coupons);
            model.addAttribute("expiringCoupons", 
                coupons.stream().filter(c -> c.isExpiringSoon()).count());

            return "shop/mypage/mypage";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "마이페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 회원정보 수정 페이지
     */
    @GetMapping("/profile")
    public String myProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 회원 정보 로드
            MemberDTO member = memberService.getMemberByUserId(userDetails.getUsername());
            model.addAttribute("member", member);

            return "shop/mypage/profile";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "회원정보를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 주문 내역 목록
     */
    @GetMapping("/orders")
    public String myOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Integer period,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            Model model) {
        try {
            // 주문 내역 로드
            Page<OrderDTO> orders = orderService.getOrdersByUserId(
                userDetails.getUsername(), period, status, keyword, pageable
            );
            model.addAttribute("orders", orders);

            // 주문 현황 로드
            Map<String, Long> orderStatus = orderService.getOrderStatusCount(userDetails.getUsername());
            model.addAttribute("orderStatus", orderStatus);

            // 검색 조건
            model.addAttribute("period", period);
            model.addAttribute("status", status);
            model.addAttribute("keyword", keyword);

            return "shop/mypage/orders";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "주문 내역을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 주문 상세 내역
     */
    @GetMapping("/orders/{id}")
    public String myOrderDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 주문 정보 로드
            OrderDTO order = orderService.getOrderById(id);
            if (order == null || !order.getUserId().equals(userDetails.getUsername())) {
                return "redirect:/mypage/orders?error=invalid_order";
            }
            model.addAttribute("order", order);

            // 배송 추적 정보 로드
            if (order.getDeliveryTracking() != null) {
                model.addAttribute("trackingInfo", 
                    orderService.getDeliveryTrackingInfo(order.getDeliveryTracking()));
            }

            return "shop/mypage/order-detail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "주문 상세 정보를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 배송지 관리 페이지
     */
    @GetMapping("/addresses")
    public String myAddresses(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 배송지 목록 로드
            List<AddressDTO> addresses = addressService.getAddressesByUserId(userDetails.getUsername());
            model.addAttribute("addresses", addresses);

            return "shop/mypage/addresses";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "배송지 정보를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 내가 작성한 리뷰 목록
     */
    @GetMapping("/reviews")
    public String myReviews(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        try {
            // 리뷰 목록 로드
            Page<ReviewDTO> reviews = reviewService.getReviewsByUserId(
                userDetails.getUsername(), pageable
            );
            model.addAttribute("reviews", reviews);

            return "shop/mypage/reviews";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "리뷰 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 포인트 내역
     */
    @GetMapping("/points")
    public String myPoints(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        try {
            // 포인트 정보 로드
            PointDTO points = pointService.getPointsByUserId(userDetails.getUsername());
            model.addAttribute("points", points);

            // 포인트 내역 로드
            Page<PointHistoryDTO> history = pointService.getPointHistory(
                userDetails.getUsername(), pageable
            );
            model.addAttribute("history", history);

            return "shop/mypage/points";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "포인트 내역을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 보유 쿠폰 목록
     */
    @GetMapping("/coupons")
    public String myCoupons(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Boolean expired,
            Model model) {
        try {
            // 쿠폰 목록 로드
            List<CouponDTO> coupons = couponService.getCouponsByUserId(
                userDetails.getUsername(), expired
            );
            model.addAttribute("coupons", coupons);
            model.addAttribute("expired", expired);

            return "shop/mypage/coupons";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "쿠폰 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 찜 목록
     */
    @GetMapping("/wishlist")
    public String myWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        try {
            // 찜 목록 로드
            Page<ProductDTO> wishlist = memberService.getWishlist(
                userDetails.getUsername(), pageable
            );
            model.addAttribute("wishlist", wishlist);

            return "shop/mypage/wishlist";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "찜 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 1:1 문의 내역
     */
    @GetMapping("/inquiries")
    public String myInquiries(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String status,
            Model model) {
        try {
            // 문의 내역 로드
            Page<InquiryDTO> inquiries = inquiryService.getInquiriesByUserId(
                userDetails.getUsername(), status, pageable
            );
            model.addAttribute("inquiries", inquiries);
            model.addAttribute("status", status);

            return "shop/mypage/inquiries";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "문의 내역을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 1:1 문의 작성
     */
    @GetMapping("/inquiries/new")
    public String newInquiry(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long productId,
            Model model) {
        try {
            // 회원 정보 로드
            MemberDTO member = memberService.getMemberByUserId(userDetails.getUsername());
            model.addAttribute("member", member);

            // 상품 정보 로드 (있는 경우)
            if (productId != null) {
                ProductDTO product = productService.getProductById(productId);
                model.addAttribute("product", product);
            }

            // 문의 유형 로드
            model.addAttribute("inquiryTypes", inquiryService.getInquiryTypes());

            return "shop/mypage/inquiry-form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "문의 작성 페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 1:1 문의 상세
     */
    @GetMapping("/inquiries/{id}")
    public String inquiryDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 문의 정보 로드
            InquiryDTO inquiry = inquiryService.getInquiryById(id);
            if (inquiry == null || !inquiry.getUserId().equals(userDetails.getUsername())) {
                return "redirect:/mypage/inquiries?error=invalid_inquiry";
            }
            model.addAttribute("inquiry", inquiry);

            return "shop/mypage/inquiry-detail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "문의 상세 정보를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }
}

