package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.ReviewDTO;
import com.peakmeshop.domain.service.CategoryService;
import com.peakmeshop.domain.service.ProductService;
import com.peakmeshop.domain.service.ReviewService;
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

import java.util.Map;

/**
 * 리뷰 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewViewController {
    private final ReviewService reviewService;
    private final ProductService productService;
    private final CategoryService categoryService;

    /**
     * 리뷰 작성 페이지
     */
    @GetMapping("/write")
    public String writeReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam(required = false) Long orderId,
            Model model) {
        try {
            // 상품 정보 로드
            ProductDTO product = productService.getProductById(productId);
            if (product == null) {
                return "error/404";
            }
            model.addAttribute("product", product);
            model.addAttribute("orderId", orderId);

            // 이미 작성한 리뷰가 있는지 확인
            if (orderId != null) {
                boolean hasReview = reviewService.hasOrderReview(orderId);
                if (hasReview) {
                    return "redirect:/mypage/reviews?error=already_exists";
                }
            }

            return "shop/review/write";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "리뷰 작성 페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 리뷰 상세
     */
    @GetMapping("/{id}")
    public String reviewDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 리뷰 정보 로드
            ReviewDTO review = reviewService.getReviewById(id);
            if (review == null) {
                return "error/404";
            }
            model.addAttribute("review", review);

            // 상품 정보 로드
            ProductDTO product = productService.getProductById(review.getProductId());
            model.addAttribute("product", product);

            // 리뷰 도움이 돼요 여부 확인
            if (userDetails != null) {
                boolean isHelpful = reviewService.isHelpful(userDetails.getUsername(), id);
                model.addAttribute("isHelpful", isHelpful);
            }

            return "shop/review/detail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "리뷰를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 리뷰 수정 페이지
     */
    @GetMapping("/{id}/edit")
    public String editReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        try {
            // 리뷰 정보 로드
            ReviewDTO review = reviewService.getReviewById(id);
            if (review == null || !review.getUserId().equals(userDetails.getUsername())) {
                return "redirect:/mypage/reviews?error=invalid_review";
            }
            model.addAttribute("review", review);

            // 상품 정보 로드
            ProductDTO product = productService.getProductById(review.getProductId());
            model.addAttribute("product", product);

            return "shop/review/edit";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "리뷰 수정 페이지를 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 베스트 리뷰 목록
     */
    @GetMapping("/best")
    public String bestReviews(
            @PageableDefault(size = 20, sort = "helpfulCount", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minRating,
            Model model) {
        try {
            // 베스트 리뷰 목록 로드
            Page<ReviewDTO> reviews = reviewService.getBestReviews(categoryId, minRating, pageable);
            model.addAttribute("reviews", reviews);

            // 필터 옵션 로드
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("minRating", minRating);

            return "shop/review/best";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "베스트 리뷰 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }

    /**
     * 포토 리뷰 목록
     */
    @GetMapping("/photo")
    public String photoReviews(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minRating,
            Model model) {
        try {
            // 포토 리뷰 목록 로드
            Page<ReviewDTO> reviews = reviewService.getPhotoReviews(categoryId, minRating, pageable);
            model.addAttribute("reviews", reviews);

            // 필터 옵션 로드
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("minRating", minRating);

            return "shop/review/photo";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "포토 리뷰 목록을 로드하는 중 오류가 발생했습니다.");
            return "error/500";
        }
    }
} 