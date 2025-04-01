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
    @Query("SELECT oi.product.id, oi.product.name, oi.product.imageUrl, SUM(oi.quantity), SUM(oi.price * oi.quantity) FROM OrderItem oi " +
            "GROUP BY oi.product.id, oi.product.name, oi.product.imageUrl " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingProducts(int limit);
}