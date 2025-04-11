package com.peakmeshop.admin.controller;

import com.peakmeshop.api.dto.*;
import com.peakmeshop.domain.entity.Admin;
import com.peakmeshop.domain.entity.AdminSession;
import com.peakmeshop.domain.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 관리자 대시보드 관련 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardViewController {

    private final AdminService adminService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final ProductService productService;
    private final CouponService couponService;
    private final PromotionService promotionService;
    private final ActivityLogService activityLogService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model
    ) {
        // 기본값 설정
        if (period == null) {
            period = "monthly";
        }

        // 주문 통계
        OrderSummaryDTO orderSummary = orderService.getOrderSummary(period, startDate, endDate);
        model.addAttribute("orderSummary", orderSummary);

        // 회원 통계
        MemberSummaryDTO memberSummary = memberService.getMemberSummary(period, startDate, endDate);
        model.addAttribute("memberSummary", memberSummary);

        // 상품 통계
        ProductSummaryDTO productSummary = productService.getProductSummary(period, startDate, endDate);
        model.addAttribute("productSummary", productSummary);

        // 쿠폰 통계
        CouponSummaryDTO couponSummary = couponService.getCouponSummary(period, startDate, endDate);
        model.addAttribute("couponSummary", couponSummary);

        // 프로모션 통계
        PromotionSummaryDTO promotionSummary = promotionService.getPromotionSummary(period, startDate, endDate);
        model.addAttribute("promotionSummary", promotionSummary);

        // 최근 주문 목록 (5개)
        List<OrderDTO> recentOrders = orderService.getRecentOrders(5);
        model.addAttribute("recentOrders", recentOrders);

        // 최근 활동 목록 (5개)
        List<ActivityLogDTO> recentActivities = activityLogService.getRecentActivities(5);
        model.addAttribute("recentActivities", recentActivities);

        // 월별 매출 데이터 (차트용)
        List<Object[]> monthlySales = orderService.getMonthlySales(
                LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).minusMonths(11),
                LocalDateTime.now()
        );

        List<String> monthLabels = new ArrayList<>();
        List<Double> salesData = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 11; i >= 0; i--) {
            String monthLabel = LocalDateTime.now().minusMonths(i).format(formatter);
            monthLabels.add(monthLabel);
            salesData.add(0.0); // 기본값 0으로 설정
        }

        // 실제 데이터로 채우기
        for (Object[] sale : monthlySales) {
            String month = (String) sale[0];
            Double total = ((Number) sale[2]).doubleValue() / 10000; // 만원 단위로 변환
            int index = monthLabels.indexOf(month);
            if (index >= 0) {
                salesData.set(index, total);
            }
        }

        model.addAttribute("monthLabels", monthLabels);
        model.addAttribute("monthlySalesData", salesData);

        // 카테고리별 판매 비율 데이터 (차트용)
        List<Object[]> categorySales = categoryService.getCategorySalesData();
        List<String> categoryLabels = new ArrayList<>();
        List<Double> categoryData = new ArrayList<>();

        for (Object[] sale : categorySales) {
            categoryLabels.add((String) sale[0]);
            categoryData.add(((Number) sale[1]).doubleValue());
        }

        model.addAttribute("categoryLabels", categoryLabels);
        model.addAttribute("categorySalesData", categoryData);

        // 기간 정보
        model.addAttribute("period", period);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/dashboard";
    }

    /**
     * 관리자 프로필 페이지
     */
    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        // 현재 로그인한 관리자 정보 가져오기
        Admin admin = adminService.getCurrentAdmin();
        model.addAttribute("admin", admin);

        // 관리자 활동 내역 가져오기
        List<ActivityLogDTO> adminActivities = activityLogService.getAdminActivities(admin.getId(), 5);
        model.addAttribute("adminActivities", adminActivities);

        // 관리자 세션 정보 가져오기
        List<AdminSession> adminSessions = adminService.getAdminSessions(admin.getId());
        model.addAttribute("adminSessions", adminSessions);

        // 현재 세션 ID 가져오기
        String currentSessionId = request.getSession().getId();
        model.addAttribute("currentSessionId", currentSessionId);

        // 2단계 인증 정보
        boolean twoFactorEnabled = admin.isTwoFactorEnabled();
        model.addAttribute("twoFactorEnabled", twoFactorEnabled);

        // 백업 코드 (2단계 인증이 활성화된 경우에만)
        if (twoFactorEnabled) {
            List<String> backupCodes = adminService.getBackupCodes(admin.getId());
            model.addAttribute("backupCodes", backupCodes);
        }

        // 알림 설정
        AdminNotificationSettingsDTO notificationSettings = adminService.getNotificationSettings(admin.getId());
        model.addAttribute("notificationSettings", notificationSettings);

        return "admin/profile";
    }

    /**
     * 프로필 정보 업데이트
     */
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute AdminProfileDTO profileDTO, RedirectAttributes redirectAttributes) {
        try {
            adminService.updateProfile(profileDTO);
            redirectAttributes.addFlashAttribute("successMessage", "프로필 정보가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "프로필 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }

    /**
     * 비밀번호 변경
     */
    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes
    ) {
        try {
            adminService.changePassword(currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("successMessage", "비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }

    /**
     * 프로필 이미지 업로드
     */
    @PostMapping("/profile/upload-image")
    public String uploadProfileImage(@RequestParam("profileImage") MultipartFile profileImage, RedirectAttributes redirectAttributes) {
        try {
            adminService.updateProfileImage(profileImage);
            redirectAttributes.addFlashAttribute("successMessage", "프로필 이미지가 성공적으로 업로드되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }

    /**
     * 2단계 인증 활성화/비활성화
     */
    @PostMapping("/profile/toggle-2fa")
    public String toggleTwoFactorAuth(
            @RequestParam boolean enabled,
            @RequestParam(required = false) String authCode,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (enabled) {
                adminService.enableTwoFactorAuth(authCode);
                redirectAttributes.addFlashAttribute("successMessage", "2단계 인증이 성공적으로 활성화되었습니다.");
            } else {
                adminService.disableTwoFactorAuth();
                redirectAttributes.addFlashAttribute("successMessage", "2단계 인증이 비활성화되었습니다.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "2단계 인증 설정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }

    /**
     * 알림 설정 업데이트
     */
    @PostMapping("/profile/update-notifications")
    public String updateNotificationSettings(@ModelAttribute AdminNotificationSettingsDTO settingsDTO, RedirectAttributes redirectAttributes) {
        try {
            adminService.updateNotificationSettings(settingsDTO);
            redirectAttributes.addFlashAttribute("successMessage", "알림 설정이 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "알림 설정 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }

    /**
     * 세션 종료
     */
    @PostMapping("/profile/terminate-session")
    public String terminateSession(@RequestParam Long sessionId, RedirectAttributes redirectAttributes) {
        try {
            adminService.terminateSession(sessionId);
            redirectAttributes.addFlashAttribute("successMessage", "세션이 성공적으로 종료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션 종료 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }

    /**
     * 모든 다른 세션 종료
     */
    @PostMapping("/profile/terminate-all-sessions")
    public String terminateAllSessions(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            Long currentSessionId = Long.valueOf(request.getSession().getId());
            adminService.terminateAllSessionsExcept(currentSessionId);
            redirectAttributes.addFlashAttribute("successMessage", "모든 다른 세션이 성공적으로 종료되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "세션 종료 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/profile";
    }
}

