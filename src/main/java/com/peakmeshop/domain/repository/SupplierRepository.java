package com.peakmeshop.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.peakmeshop.domain.entity.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByCode(String code);

    Page<Supplier> findByStatus(String status, Pageable pageable);

    List<Supplier> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
}