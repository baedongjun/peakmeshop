package com.peakmeshop.domain.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.peakmeshop.api.dto.ContractDTO;
import com.peakmeshop.api.dto.SettlementDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.peakmeshop.api.dto.ProductDTO;
import com.peakmeshop.api.dto.SupplierDTO;
import com.peakmeshop.api.mapper.ProductMapper;
import com.peakmeshop.api.mapper.SupplierMapper;
import com.peakmeshop.domain.entity.Product;
import com.peakmeshop.domain.entity.Supplier;
import com.peakmeshop.domain.entity.SupplierProduct;
import com.peakmeshop.domain.repository.ProductRepository;
import com.peakmeshop.domain.repository.SupplierProductRepository;
import com.peakmeshop.domain.repository.SupplierRepository;
import com.peakmeshop.domain.service.SupplierService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final SupplierProductRepository supplierProductRepository;
    private final SupplierMapper supplierMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(supplierMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> getActiveSuppliers(Pageable pageable) {
        return supplierRepository.findByIsActiveTrue(pageable)
                .map(supplierMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierDTO> getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(supplierMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierDTO> getSupplierByCode(String code) {
        return supplierRepository.findByCode(code)
                .map(supplierMapper::toDTO);
    }

    @Override
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier.setCreatedAt(LocalDateTime.now());

        // 공급업체 코드가 없는 경우 자동 생성
        if (supplier.getCode() == null || supplier.getCode().isEmpty()) {
            supplier.setCode(generateSupplierCode(supplier.getName()));
        }

        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("공급업체 생성: ID={}, 이름={}, 코드={}",
                savedSupplier.getId(), savedSupplier.getName(), savedSupplier.getCode());

        return supplierMapper.toDTO(savedSupplier);
    }

    @Override
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        if (!supplierRepository.existsById(id)) {
            throw new IllegalArgumentException("공급업체를 찾을 수 없습니다: " + id);
        }

        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        supplier.setId(id);
        supplier.setUpdatedAt(LocalDateTime.now());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("공급업체 업데이트: ID={}, 이름={}, 코드={}",
                updatedSupplier.getId(), updatedSupplier.getName(), updatedSupplier.getCode());

        return supplierMapper.toDTO(updatedSupplier);
    }

    @Override
    public boolean deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            return false;
        }

        try {
            // 공급업체-제품 연결 정보 삭제
            supplierProductRepository.deleteBySupplier_Id(id);
            // 공급업체 삭제
            supplierRepository.deleteById(id);
            log.info("공급업체 삭제: ID={}", id);
            return true;
        } catch (Exception e) {
            log.error("공급업체 삭제 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getSupplierProducts(Long supplierId, Pageable pageable) {
        return supplierProductRepository.findProductsBySupplier_Id(supplierId, pageable)
                .map(productMapper::toDTO);
    }

    @Override
    public void addProductToSupplier(Long supplierId, Long productId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("공급업체를 찾을 수 없습니다: " + supplierId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다: " + productId));

        // 이미 연결되어 있는지 확인
        if (supplierProductRepository.existsBySupplier_IdAndProduct_Id(supplierId, productId)) {
            log.info("이미 공급업체에 제품이 연결되어 있습니다: 공급업체 ID={}, 제품 ID={}", supplierId, productId);
            return;
        }

        SupplierProduct supplierProduct = new SupplierProduct();
        supplierProduct.setSupplier(supplier);
        supplierProduct.setProduct(product);
        supplierProduct.setCreatedAt(LocalDateTime.now());

        supplierProductRepository.save(supplierProduct);
        log.info("공급업체에 제품 추가: 공급업체 ID={}, 제품 ID={}", supplierId, productId);
    }

    @Override
    public void removeProductFromSupplier(Long supplierId, Long productId) {
        if (!supplierProductRepository.existsBySupplier_IdAndProduct_Id(supplierId, productId)) {
            log.info("공급업체에 제품이 연결되어 있지 않습니다: 공급업체 ID={}, 제품 ID={}", supplierId, productId);
            return;
        }

        supplierProductRepository.deleteBySupplier_IdAndProduct_Id(supplierId, productId);
        log.info("공급업체에서 제품 제거: 공급업체 ID={}, 제품 ID={}", supplierId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> searchSuppliers(String keyword) {
        return supplierRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(keyword, keyword).stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> getSupplierSummary() {
        // 이번 달 신규 등록 공급사 수
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        Map<String, Long> summary = new HashMap<>();
        summary.put("totalSuppliers", supplierRepository.count());
        summary.put("activeSuppliers", supplierRepository.countActiveSuppliers());
        summary.put("newSuppliers", supplierRepository.countNewSuppliers(startOfMonth, endOfMonth));
        summary.put("totalProducts", productRepository.count());
        summary.put("totalSales", calculateTotalSales());
        return summary;
    }

    @Override
    public Map<String, Long> getSupplierSummary(Long supplierId) {
        Map<String, Long> summary = new HashMap<>();
        summary.put("totalProducts", supplierRepository.countProductsBySupplierId(supplierId));
        summary.put("activeProducts", supplierRepository.countActiveProductsBySupplierId(supplierId));
        summary.put("totalOrders", supplierRepository.countTotalOrdersBySupplierId(supplierId));
        summary.put("totalSales", supplierRepository.calculateTotalSalesBySupplierId(supplierId));
        return summary;
    }

    @Override
    public Page<SettlementDTO> getSettlements(Pageable pageable) {
        return null;
    }

    @Override
    public Page<ContractDTO> getContracts(Pageable pageable) {
        return null;
    }

    @Override
    public ContractDTO getContractById(Long id) {
        return null;
    }

    private String generateSupplierCode(String name) {
        // 공급업체 코드 생성 로직 구현
        // 예: 이름의 첫 글자 + 현재 시간의 밀리초
        return name.substring(0, 1).toUpperCase() + System.currentTimeMillis();
    }

    private Long calculateTotalSales() {
        // 전체 매출 계산 로직 구현
        return 0L;
    }
}