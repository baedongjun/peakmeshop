package com.peakmeshop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.peakmeshop.entity.MemberTier;

@Repository
public interface MemberTierRepository extends JpaRepository<MemberTier, Long> {

    Optional<MemberTier> findByCode(String code);

    Boolean existsByCode(String code);

    List<MemberTier> findByIsActiveTrue(Sort sort);

    @Query("SELECT MAX(mt.orderIndex) FROM MemberTier mt")
    Integer findMaxOrderIndex();
}