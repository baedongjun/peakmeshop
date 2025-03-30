package com.peakmeshop.service.impl;

import com.peakmeshop.dto.StockNotificationDTO;
import com.peakmeshop.entity.Member;
import com.peakmeshop.entity.Product;
import com.peakmeshop.entity.ProductImage;
import com.peakmeshop.entity.StockNotification;
import com.peakmeshop.repository.MemberRepository;
import com.peakmeshop.repository.ProductRepository;
import com.peakmeshop.repository.StockNotificationRepository;
import com.peakmeshop.service.EmailService;
import com.peakmeshop.service.StockNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockNotificationServiceImpl implements StockNotificationService {

    private final StockNotificationRepository stockNotificationRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final EmailService emailService;

    public StockNotificationServiceImpl(
            StockNotificationRepository stockNotificationRepository,
            ProductRepository productRepository,
            MemberRepository memberRepository,
            EmailService emailService) {
        this.stockNotificationRepository = stockNotificationRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public boolean subscribeToStockNotification(Long productId, Long memberId) {
        // 이미 구독 중인지 확인
        if (stockNotificationRepository.existsByProductIdAndMemberId(productId, memberId)) {
            return false;
        }

        // 상품과 회원 존재 확인
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<Member> memberOpt = memberRepository.findById(memberId);

        if (productOpt.isEmpty() || memberOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();
        Member member = memberOpt.get();

        // 이미 재고가 있는 경우 구독 불필요
        if (product.isInStock()) {
            return false;
        }

        // 새 알림 생성
        StockNotification notification = StockNotification.builder()
                .product(product)
                .member(member)
                .createdAt(LocalDateTime.now())
                .isActive(true)
                .build();

        stockNotificationRepository.save(notification);

        log.info("Member {} subscribed to stock notification for product {}", memberId, productId);
        return true;
    }

    @Override
    @Transactional
    public boolean unsubscribeFromStockNotification(Long productId, Long memberId) {
        Optional<StockNotification> notificationOpt = stockNotificationRepository.findByProductIdAndMemberId(productId, memberId);

        if (notificationOpt.isEmpty()) {
            return false;
        }

        StockNotification notification = notificationOpt.get();
        stockNotificationRepository.delete(notification);

        log.info("Member {} unsubscribed from stock notification for product {}", memberId, productId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockNotificationDTO> getStockNotificationsByMemberId(Long memberId) {
        List<StockNotification> notifications = stockNotificationRepository.findByMemberIdAndIsActiveTrue(memberId);

        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockNotificationDTO> getStockNotificationsByProductId(Long productId) {
        List<StockNotification> notifications = stockNotificationRepository.findByProductIdAndIsActiveTrue(productId);

        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSubscribed(Long productId, Long memberId) {
        return stockNotificationRepository.existsByProductIdAndMemberId(productId, memberId);
    }

    @Override
    @Transactional
    public int sendStockNotifications(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty() || !productOpt.get().isInStock()) {
            return 0;
        }

        Product product = productOpt.get();
        List<StockNotification> notifications = stockNotificationRepository.findByProductIdAndIsActiveTrue(productId);

        for (StockNotification notification : notifications) {
            Member member = notification.getMember();

            // 이메일 발송
            try {
                // 재고 알림 이메일 발송 로직
                // emailService.sendStockNotificationEmail(member.getEmail(), member.getName(), product.getName());

                // 알림 상태 업데이트
                notification.markAsNotified();
            } catch (Exception e) {
                log.error("Failed to send stock notification email to member {}", member.getId(), e);
            }
        }

        stockNotificationRepository.saveAll(notifications);

        log.info("Sent stock notifications for product {} to {} members", productId, notifications.size());
        return notifications.size();
    }

    @Override
    @Transactional
    public void processStockNotificationsForProduct(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty()) {
            return;
        }

        Product product = productOpt.get();

        if (product.isInStock()) {
            sendStockNotifications(productId);
        }
    }

    @Override
    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 실행
    @Transactional
    public void cleanupOldNotifications() {
        // 30일 이상 지난 알림 삭제
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        int deletedCount = stockNotificationRepository.deleteOldNotifications(thirtyDaysAgo);

        log.info("Cleaned up {} old stock notifications", deletedCount);
    }

    private StockNotificationDTO convertToDTO(StockNotification notification) {
        String imageUrl = null;
        List<String> images = notification.getProduct().getImages();
        if (images != null && !images.isEmpty()) {
            imageUrl = images.get(0); // 첫 번째 이미지 URL 사용
        }

        return new StockNotificationDTO(
                notification.getId(),
                notification.getProduct().getId(),
                notification.getProduct().getName(),
                imageUrl,
                notification.getMember().getId(),
                notification.getMember().getName(),
                notification.getMember().getEmail(),
                notification.getCreatedAt(),
                notification.getNotifiedAt(),
                notification.isActive()
        );
    }
}