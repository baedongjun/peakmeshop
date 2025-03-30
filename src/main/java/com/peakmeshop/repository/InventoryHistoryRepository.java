package com.peakmeshop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peakmeshop.entity.InventoryHistory;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {

    Page<InventoryHistory> findByInventoryProductId(Long productId, Pageable pageable);
}