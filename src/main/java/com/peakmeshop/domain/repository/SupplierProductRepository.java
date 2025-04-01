package com.peakmeshop.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.SupplierProduct;

@Repository
public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {

    boolean existsBySupplier_IdAndProduct_Id(Long supplierId, Long productId);

    void deleteBySupplier_IdAndProduct_Id(Long supplierId, Long productId);

    void deleteBySupplier_Id(Long supplierId);

    @Query("SELECT sp.product FROM SupplierProduct sp WHERE sp.supplier.id = :supplierId")
    Page<Product> findProductsBySupplier_Id(Long supplierId, Pageable pageable);
}