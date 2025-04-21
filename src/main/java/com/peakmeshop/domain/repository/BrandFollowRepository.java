package com.peakmeshop.domain.repository;

import com.peakmeshop.domain.entity.BrandFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandFollowRepository extends JpaRepository<BrandFollow, Long> {
    boolean existsByUsernameAndBrandId(String username, Long brandId);
} 