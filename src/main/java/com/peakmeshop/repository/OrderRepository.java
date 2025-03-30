package com.peakmeshop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.peakmeshop.enums.OrderStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.peakmeshop.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Page<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime createdAt);

    long countByStatus(OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Double calculateSalesAmountBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // 특정 기간 동안의 주문 수 계산
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 주문 상태별 분포 조회
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> findOrderStatusDistribution();

    // 일별 매출 조회
    @Query("SELECT CAST(o.createdAt AS date), SUM(o.totalAmount) FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(o.createdAt AS date) " +
            "ORDER BY CAST(o.createdAt AS date)")
    List<Object[]> findDailySalesBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // 월별 매출 조회
    @Query("SELECT YEAR(o.createdAt), MONTH(o.createdAt), SUM(o.totalAmount) FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " +
            "ORDER BY YEAR(o.createdAt), MONTH(o.createdAt)")
    List<Object[]> findMonthlySalesBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // 우수 고객 조회
    @Query("SELECT o.member.id, o.member.name, o.member.email, COUNT(o), SUM(o.totalAmount) FROM Order o " +
            "WHERE o.member IS NOT NULL " +
            "GROUP BY o.member.id, o.member.name, o.member.email " +
            "ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> findTopCustomers(int limit);
}