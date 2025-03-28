package com.peakmeshop.scheduler;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.peakmeshop.service.OrderService;
import com.peakmeshop.service.ProductService;

@Component
@EnableScheduling
public class ScheduledTasks {

    private final OrderService orderService;
    private final ProductService productService;

    public ScheduledTasks(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredCarts() {
        orderService.cleanupAbandonedCarts();
    }

    // 매시간 실행
    @Scheduled(cron = "0 0 * * * ?")
    public void updateProductInventory() {
        productService.syncInventory();
    }

    // 매주 월요일 오전 1시에 실행
    @Scheduled(cron = "0 0 1 ? * MON")
    public void generateWeeklyReport() {
        orderService.generateWeeklyReport();
    }
}