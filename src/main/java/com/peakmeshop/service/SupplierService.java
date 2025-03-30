package com.peakmeshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.peakmeshop.dto.ProductDTO;
import com.peakmeshop.dto.SupplierDTO;

public interface SupplierService {

    Page<SupplierDTO> getAllSuppliers(Pageable pageable);

    Page<SupplierDTO> getActiveSuppliers(Pageable pageable);

    Optional<SupplierDTO> getSupplierById(Long id);

    Optional<SupplierDTO> getSupplierByCode(String code);

    SupplierDTO createSupplier(SupplierDTO supplierDTO);

    SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO);

    boolean deleteSupplier(Long id);

    Page<ProductDTO> getSupplierProducts(Long supplierId, Pageable pageable);

    void addProductToSupplier(Long supplierId, Long productId);

    void removeProductFromSupplier(Long supplierId, Long productId);

    List<SupplierDTO> searchSuppliers(String keyword);
}