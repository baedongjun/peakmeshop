package com.peakmeshop.domain.repository;

import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.order.id = :orderId")
    void deleteByOrderId(Long orderId);

    // 함께 구매한 상품 ID 조회
    @Query(value = "SELECT oi2.product_id FROM order_items oi1 " +
            "JOIN order_items oi2 ON oi1.order_id = oi2.order_id " +
            "WHERE oi1.product_id = :productId AND oi2.product_id != :productId " +
            "GROUP BY oi2.product_id " +
            "ORDER BY COUNT(oi2.product_id) DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Long> findFrequentlyBoughtTogetherProductIds(@Param("productId") Long productId, @Param("limit") int limit);

    // 인기 상품 조회
    @Query("SELECT oi.product.id, oi.product.name, oi.product.mainImage, SUM(oi.quantity), SUM(oi.price * oi.quantity) FROM OrderItem oi " +
            "GROUP BY oi.product.id, oi.product.name, oi.product.mainImage " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProducts(int limit);

    /**
     * 카테고리별 판매 비율을 조회합니다.
     * @return 카테고리명과 판매 비율(%)을 포함하는 배열의 목록
     */
    @Query(value =
            "SELECT c.name, " +
                    "ROUND(COUNT(oi.id) * 100.0 / (SELECT COUNT(*) FROM order_items), 2) as percentage " +
                    "FROM order_items oi " +
                    "JOIN products p ON oi.product_id = p.id " +
                    "JOIN categories c ON p.category_id = c.id " +
                    "GROUP BY c.name " +
                    "ORDER BY percentage DESC",
            nativeQuery = true)
    List<Object[]> findCategorySalesPercentage();

    /**
     * 카테고리별 매출 금액을 조회합니다.
     * @return 카테고리명과 매출 금액을 포함하는 배열의 목록
     */
    @Query(value =
            "SELECT c.name, SUM(oi.price * oi.quantity) as revenue " +
                    "FROM order_items oi " +
                    "JOIN products p ON oi.product_id = p.id " +
                    "JOIN categories c ON p.category_id = c.id " +
                    "JOIN orders o ON oi.order_id = o.id " +
                    "WHERE o.status != 'CANCELLED' " +
                    "GROUP BY c.name " +
                    "ORDER BY revenue DESC",
            nativeQuery = true)
    List<Object[]> findCategoryRevenue();

    /**
     * 상품별 판매 수량을 조회합니다.
     * @return 상품 ID와 판매 수량을 포함하는 배열의 목록
     */
    @Query("SELECT oi.product.id, SUM(oi.quantity) FROM OrderItem oi GROUP BY oi.product.id")
    List<Object[]> findProductSalesQuantity();

}