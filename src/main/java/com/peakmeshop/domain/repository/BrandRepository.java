package com.peakmeshop.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findBySlug(String slug);

    List<Brand> findByIsActiveTrueOrderByNameAsc();

    List<Brand> findByIsFeaturedTrueAndIsActiveTrueOrderByNameAsc();

    Page<Brand> findByIsActiveTrue(Pageable pageable);

    Page<Brand> findByIsFeaturedTrue(Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Brand> searchBrands(String keyword, Pageable pageable);

    @Query("SELECT b FROM Brand b LEFT JOIN b.products p GROUP BY b.id ORDER BY COUNT(p) DESC")
    List<Brand> findTopBrandsByProductCount(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Brand b JOIN b.products p WHERE b.id = :brandId")
    int countProductsByBrandId(Long brandId);
}

