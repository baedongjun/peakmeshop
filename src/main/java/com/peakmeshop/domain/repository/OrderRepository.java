package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.Order;
import com.peakmeshop.domain.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @Query(value = "SELECT DATE(o.created_at) as date, " +
            "COUNT(o) as count, " +
            "SUM(o.final_price) as total " +
            "FROM orders o " +
            "WHERE o.created_at BETWEEN :startDate AND :endDate " +
            "AND o.status != 'CANCELLED' " +
            "GROUP BY DATE(o.created_at) " +
            "ORDER BY date", nativeQuery = true)
    List<Object[]> getDailySales(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    /**
     * 월별 매출 통계 조회
     * @param startDate 시작일
     * @param endDate 종료일
     * @return [년월, 주문수, 총액] 형태의 통계 데이터
     */
    @Query(value = "SELECT TO_CHAR(o.created_at, 'YYYY-MM') as month, " +
            "COUNT(o) as count, " +
            "SUM(o.total_amount) as total " +
            "FROM orders o " +
            "WHERE o.created_at BETWEEN :startDate AND :endDate " +
            "AND o.status != 'CANCELLED' " +
            "GROUP BY TO_CHAR(o.created_at, 'YYYY-MM') " +
            "ORDER BY month", nativeQuery = true)
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
    @Query("SELECT COALESCE(SUM(o.finalPrice), 0) FROM Order o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status != 'CANCELLED'")
    Double calculateTotalRevenue(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    /**
     * 기간별 주문 수 조회
     */
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 상태이고 특정 시간 이후에 업데이트된 주문을 조회합니다.
     * @param status 주문 상태
     * @param updatedAt 업데이트 시간
     * @param pageable 페이징 정보
     * @return 조건에 맞는 주문 목록
     */
    Page<Order> findByStatusAndUpdatedAtAfter(String status, LocalDateTime updatedAt, Pageable pageable);

    /**
     * 특정 상태인 주문을 조회합니다.
     * @param status 주문 상태
     * @param pageable 페이징 정보
     * @return 조건에 맞는 주문 목록
     */
    Page<Order> findByStatus(String status, Pageable pageable);

    /**
     * 특정 회원의 주문을 조회합니다.
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 조건에 맞는 주문 목록
     */
    Page<Order> findByMemberId(Long memberId, Pageable pageable);

    /**
     * 특정 기간 내의 주문을 조회합니다.
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param pageable 페이징 정보
     * @return 조건에 맞는 주문 목록
     */
    Page<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 특정 상태이고 특정 기간 내의 주문을 조회합니다.
     * @param status 주문 상태
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param pageable 페이징 정보
     * @return 조건에 맞는 주문 목록
     */
    Page<Order> findByStatusAndCreatedAtBetween(String status, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}