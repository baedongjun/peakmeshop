package com.peakmeshop.shop.controller;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.PromotionDTO;
import com.peakmeshop.domain.service.ProductService;
import com.peakmeshop.domain.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * 이벤트 및 프로모션 관련 뷰 컨트롤러
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/event")
@Slf4j
public class EventViewController {

    private final PromotionService promotionService;
    private final ProductService productService;

    /**
     * 이벤트 목록
     */
    @GetMapping("/list")
    public String eventList(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 이벤트/프로모션 목록을 조회 (현재 활성화된 프로모션만)
        Page<PromotionDTO> events = promotionService.getActivePromotions(pageable);

        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword);

        return "shop/event/event-list";
    }

    /**
     * 이벤트 상세
     */
    @GetMapping("/{id}")
    public String eventDetail(@PathVariable Long id, Model model) {
        try {
            // 이벤트 정보 조회
            Optional<PromotionDTO> eventOpt = promotionService.getPromotionById(id);

            if (eventOpt.isPresent()) {
                PromotionDTO event = eventOpt.get();
                model.addAttribute("event", event);

                // 관련 상품이 있는 경우 상품 정보도 조회
                if (event.getProductId() != null) {
                    model.addAttribute("product", productService.getProductById(event.getProductId()));
                }

                // 관련 카테고리가 있는 경우 카테고리 상품 추천
                if (event.getCategoryId() != null) {
                    Page<ProductDTO> categoryProducts = productService.getProductsByCategory(
                            event.getCategoryId(),
                            PageRequest.of(0, 4, Sort.by("createdAt").descending())
                    );
                    model.addAttribute("categoryProducts", categoryProducts);
                }

                return "shop/event/event-detail";
            } else {
                log.error("이벤트를 찾을 수 없습니다: {}", id);
                model.addAttribute("message", "이벤트를 찾을 수 없습니다.");
                model.addAttribute("messageType", "danger");
                return "redirect:/shop/event/list";
            }
        } catch (Exception e) {
            log.error("이벤트 상세 조회 중 오류 발생: {}", e.getMessage());
            model.addAttribute("message", "이벤트 정보를 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("messageType", "danger");
            return "redirect:/shop/event/list";
        }
    }

    /**
     * 프로모션 목록
     */
    @GetMapping("/promotions")
    public String promotionList(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 9, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 프로모션 목록 조회
        Page<PromotionDTO> promotions;

        if (keyword != null && !keyword.isEmpty()) {
            promotions = promotionService.getPromotions(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            promotions = promotionService.getAllPromotions(pageable);
        }

        model.addAttribute("promotions", promotions);

        return "shop/event/promotion-list";
    }

    /**
     * 프로모션 상세
     */
    @GetMapping("/promotion/{id}")
    public String promotionDetail(@PathVariable Long id, Model model) {
        try {
            // 프로모션 정보 조회
            Optional<PromotionDTO> promotionOpt = promotionService.getPromotionById(id);

            if (promotionOpt.isPresent()) {
                PromotionDTO promotion = promotionOpt.get();
                model.addAttribute("promotion", promotion);

                // 프로모션 적용 상품이 있는 경우 상품 정보도 조회
                if (promotion.getProductId() != null) {
                    model.addAttribute("product", productService.getProductById(promotion.getProductId()));
                }

                // 프로모션 적용 카테고리가 있는 경우 카테고리 상품 목록 조회
                if (promotion.getCategoryId() != null) {
                    Page<ProductDTO> categoryProducts = productService.getProductsByCategory(
                            promotion.getCategoryId(),
                            PageRequest.of(0, 8, Sort.by("createdAt").descending())
                    );
                    model.addAttribute("categoryProducts", categoryProducts);
                }

                return "shop/event/promotion-detail";
            } else {
                log.error("프로모션을 찾을 수 없습니다: {}", id);
                model.addAttribute("message", "프로모션을 찾을 수 없습니다.");
                model.addAttribute("messageType", "danger");
                return "redirect:/shop/event/promotions";
            }
        } catch (Exception e) {
            log.error("프로모션 상세 조회 중 오류 발생: {}", e.getMessage());
            model.addAttribute("message", "프로모션 정보를 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("messageType", "danger");
            return "redirect:/shop/event/promotions";
        }
    }

    /**
     * 진행 중인 이벤트 목록
     */
    @GetMapping("/ongoing")
    public String ongoingEvents(
            @PageableDefault(size = 9, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 현재 진행 중인 이벤트/프로모션 조회
        Page<PromotionDTO> ongoingEvents = promotionService.getActivePromotions(pageable);

        model.addAttribute("events", ongoingEvents);
        model.addAttribute("pageTitle", "진행 중인 이벤트");

        return "shop/event/event-list";
    }

    /**
     * 종료된 이벤트 목록
     */
    @GetMapping("/ended")
    public String endedEvents(
            @PageableDefault(size = 9, sort = "endDate", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {

        // 종료된 이벤트/프로모션 조회는 PromotionService에서 직접 구현되지 않았으므로, 
        // 모든 프로모션을 가져와 클라이언트 측에서 필터링하게 함
        Page<PromotionDTO> allEvents = promotionService.getAllPromotions(pageable);

        model.addAttribute("events", allEvents);
        model.addAttribute("pageTitle", "종료된 이벤트");
        model.addAttribute("showEnded", true);

        return "shop/event/event-list";
    }
}

