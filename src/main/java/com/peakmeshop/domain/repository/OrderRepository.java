package com.peakmeshop.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Query("SELECT o FROM Order o WHERE o.member.id = :memberId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByMemberIdAndDateRange(@Param("memberId") Long memberId, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.member.id = :memberId AND o.status = :status")
    Page<Order> findByMemberIdAndStatus(@Param("memberId") Long memberId, 
                                      @Param("status") OrderStatus status, 
                                      Pageable pageable);

    @Query("SELECT o.status as status, COUNT(o) as count FROM Order o GROUP BY o.status")
    List<Object[]> findOrderStatusDistribution();

    @Query("SELECT DATE(o.createdAt) as date, COUNT(o) as count, SUM(o.finalPrice) as total " +
           "FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(o.createdAt)")
    List<Object[]> findDailySalesBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') as month, " +
           "COUNT(o) as count, SUM(o.finalPrice) as total " +
           "FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')")
    List<Object[]> findMonthlySalesBetween(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT o.member.id as memberId, o.member.name as memberName, " +
           "COUNT(o) as orderCount, SUM(o.finalPrice) as totalAmount " +
           "FROM Order o GROUP BY o.member.id, o.member.name ORDER BY COUNT(o) DESC")
    List<Object[]> findTopCustomers(Pageable pageable);

    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, OrderStatus status, Pageable pageable);

    /**
     * 일별 매출 통계 조회
     * @param startDate 시작일
     * @param endDate 종료일
     * @return [날짜, 주문수, 총액] 형태의 통계 데이터
     */
    @Query("SELECT DATE(o.createdAt) as date, " +
           "COUNT(o) as count, " +
           "SUM(o.totalAmount) as total " +
           "FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.status != 'CANCELLED' " +
           "GROUP BY DATE(o.createdAt) " +
           "ORDER BY date")
    List<Object[]> getDailySales(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    /**
     * 월별 매출 통계 조회
     * @param startDate 시작일
     * @param endDate 종료일
     * @return [년월, 주문수, 총액] 형태의 통계 데이터
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') as month, " +
           "COUNT(o) as count, " +
           "SUM(o.totalAmount) as total " +
           "FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.status != 'CANCELLED' " +
           "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') " +
           "ORDER BY month")
    List<Object[]> getMonthlySales(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    /**
     * 주문 상태별 분포 조회
     * @return [상태, 주문수] 형태의 통계 데이터
     */
    @Query("SELECT o.status as status, " +
           "COUNT(o) as count " +
           "FROM Order o " +
           "GROUP BY o.status")
    Map<OrderStatus, Long> getOrderStatusDistribution();

    /**
     * 특정 기간의 주문 목록 조회
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByDateRange(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    /**
     * 특정 기간의 총 매출액 계산
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
           "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.status != 'CANCELLED'")
    double calculateTotalRevenue(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    /**
     * 기간별 주문 수 조회
     */
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}