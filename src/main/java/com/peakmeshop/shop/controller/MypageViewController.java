package com.peakmeshop.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 마이페이지 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/mypage")
public class MypageViewController {

    /**
     * 마이페이지 메인
     */
    @GetMapping
    public String myPage() {
        return "shop/mypage";
    }

    /**
     * 회원정보 수정 페이지
     */
    @GetMapping("/profile")
    public String myProfile() {
        return "shop/my-profile";
    }

    /**
     * 주문 내역 목록
     */
    @GetMapping("/orders")
    public String myOrders() {
        return "shop/my-orders";
    }

    /**
     * 주문 상세 내역
     */
    @GetMapping("/orders/{id}")
    public String myOrderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("orderId", id);
        return "shop/order-detail";
    }

    /**
     * 배송지 관리 페이지
     */
    @GetMapping("/addresses")
    public String myAddresses() {
        return "shop/my-addresses";
    }

    /**
     * 내가 작성한 리뷰 목록
     */
    @GetMapping("/reviews")
    public String myReviews() {
        return "shop/my-reviews";
    }

    /**
     * 포인트 내역
     */
    @GetMapping("/points")
    public String myPoints() {
        return "shop/my-points";
    }

    /**
     * 보유 쿠폰 목록
     */
    @GetMapping("/coupons")
    public String myCoupons() {
        return "shop/my-coupons";
    }

    /**
     * 찜 목록
     */
    @GetMapping("/wishlist")
    public String myWishlist() {
        return "shop/my-wishlist";
    }

    /**
     * 1:1 문의 내역
     */
    @GetMapping("/inquiries")
    public String myInquiries() {
        return "shop/my-inquiries";
    }

    /**
     * 1:1 문의 작성
     */
    @GetMapping("/inquiries/new")
    public String newInquiry() {
        return "shop/inquiry-form";
    }

    /**
     * 1:1 문의 상세
     */
    @GetMapping("/inquiries/{id}")
    public String inquiryDetail(@PathVariable Long id, Model model) {
        model.addAttribute("inquiryId", id);
        return "shop/inquiry-detail";
    }
}

