package com.peakmeshop.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.peakmeshop.domain.entity.Order;
import com.peakmeshop.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Page<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    Page<Order> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime dateTime);

    long countByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.member.id = :memberId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByMemberIdAndDateRange(@Param("memberId") Long memberId, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.member.id = :memberId AND o.status = :status")
    Page<Order> findByMemberIdAndStatus(@Param("memberId") Long memberId, 
                                      @Param("status") OrderStatus status, 
                                      Pageable pageable);

    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> findOrderStatusDistribution();

    @Query("SELECT DATE(o.createdAt), COUNT(o), SUM(o.finalPrice) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(o.createdAt)")
    List<Object[]> findDailySalesBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT YEAR(o.createdAt), MONTH(o.createdAt), COUNT(o), SUM(o.finalPrice) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)")
    List<Object[]> findMonthlySalesBetween(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o.member.id, o.member.name, COUNT(o), SUM(o.finalPrice) FROM Order o GROUP BY o.member.id, o.member.name ORDER BY COUNT(o) DESC")
    List<Object[]> findTopCustomers(Pageable pageable);

    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, OrderStatus status, Pageable pageable);
}