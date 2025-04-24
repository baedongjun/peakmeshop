package com.peakmeshop.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByCode(String code);

    Page<Supplier> findByIsActiveTrue(Pageable pageable);

    List<Supplier> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);

    // 활성 공급사 수 조회
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.isActive = true")
    long countActiveSuppliers();

    // 이번 달 신규 등록 공급사 수 조회
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    long countNewSuppliers(LocalDateTime startDate, LocalDateTime endDate);

    // 공급사별 상품 수 조회
    @Query("SELECT s.id, COUNT(p) FROM Supplier s LEFT JOIN s.products p GROUP BY s.id")
    List<Object[]> countProductsBySupplier();

    // 공급사별 총 매출 조회
    @Query(value = "SELECT s.id, SUM(oi.quantity * oi.price) " +
            "FROM suppliers s " +
            "JOIN products p ON p.supplier_id = s.id " +
            "JOIN order_items oi ON oi.product_id = p.id " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.status IN ('COMPLETED', 'DELIVERED') " +
            "GROUP BY s.id", nativeQuery = true)
    List<Object[]> calculateTotalSalesBySupplier();

    // 특정 공급사의 총 상품 수 조회
    @Query("SELECT COUNT(p) FROM Product p WHERE p.supplier.id = :supplierId")
    long countProductsBySupplierId(Long supplierId);

    // 특정 공급사의 총 매출 조회
    @Query(value = "SELECT SUM(oi.quantity * oi.price) " +
            "FROM products p " +
            "JOIN order_items oi ON oi.product_id = p.id " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE p.supplier_id = :supplierId AND o.status IN ('COMPLETED', 'DELIVERED')",
            nativeQuery = true)
    Long calculateTotalSalesBySupplierId(Long supplierId);

    // 특정 공급사의 활성 상품 수 조회
    @Query("SELECT COUNT(p) FROM Product p WHERE p.supplier.id = :supplierId AND p.status = 'ACTIVE'")
    long countActiveProductsBySupplierId(Long supplierId);

    // 특정 공급사의 총 주문 수 조회
    @Query(value = "SELECT COUNT(DISTINCT o.id) " +
            "FROM suppliers s " +
            "JOIN products p ON p.supplier_id = s.id " +
            "JOIN order_items oi ON oi.product_id = p.id " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE s.id = :supplierId",
            nativeQuery = true)
    long countTotalOrdersBySupplierId(Long supplierId);

    // 키워드로 공급사 검색 (이름, 코드, 사업자번호)
    @Query("SELECT s FROM Supplier s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.businessNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Supplier> searchSuppliers(String keyword, Pageable pageable);
}