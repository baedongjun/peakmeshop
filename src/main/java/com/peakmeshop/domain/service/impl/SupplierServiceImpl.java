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

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> getActiveSuppliers(Pageable pageable) {
        return supplierRepository.findByStatus("ACTIVE", pageable)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierDTO> getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SupplierDTO> getSupplierByCode(String code) {
        return supplierRepository.findByCode(code)
                .map(this::convertToDTO);
    }

    @Override
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = convertToEntity(supplierDTO);
        supplier.setCreatedAt(LocalDateTime.now());

        // 공급업체 코드가 없는 경우 자동 생성
        if (supplier.getCode() == null || supplier.getCode().isEmpty()) {
            supplier.setCode(generateSupplierCode(supplier.getName()));
        }

        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("공급업체 생성: ID={}, 이름={}, 코드={}",
                savedSupplier.getId(), savedSupplier.getName(), savedSupplier.getCode());

        return convertToDTO(savedSupplier);
    }

    @Override
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공급업체를 찾을 수 없습니다: " + id));

        updateSupplierFromDTO(supplier, supplierDTO);
        supplier.setUpdatedAt(LocalDateTime.now());

        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("공급업체 업데이트: ID={}, 이름={}, 코드={}",
                updatedSupplier.getId(), updatedSupplier.getName(), updatedSupplier.getCode());

        return convertToDTO(updatedSupplier);
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
                .map(this::convertToProductDTO);
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
                .map(this::convertToDTO)
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

    // 헬퍼 메서드
    private SupplierDTO convertToDTO(Supplier supplier) {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(supplier.getId());
        dto.setCode(supplier.getCode());
        dto.setName(supplier.getName());
        dto.setBusinessNumber(supplier.getBusinessNumber());
        dto.setRepresentativeName(supplier.getRepresentativeName());
        dto.setEmail(supplier.getEmail());
        dto.setPhone(supplier.getPhone());
        dto.setAddress1(supplier.getAddress1());
        dto.setAddress2(supplier.getAddress2());
        dto.setBankName(supplier.getBankName());
        dto.setAccountNumber(supplier.getAccountNumber());
        dto.setAccountHolder(supplier.getAccountHolder());
        dto.setCity(supplier.getCity());
        dto.setState(supplier.getState());
        dto.setZipCode(supplier.getZipCode());
        dto.setCountry(supplier.getCountry());
        dto.setWebsite(supplier.getWebsite());
        dto.setDescription(supplier.getDescription());
        dto.setStatus(supplier.getStatus());
        dto.setCreatedAt(supplier.getCreatedAt());
        dto.setUpdatedAt(supplier.getUpdatedAt());

        // 추가 정보 설정
        try {
            // 공급사별 상품 수
            long productCount = supplierRepository.countProductsBySupplierId(supplier.getId());
            dto.setProductCount(productCount);

            // 공급사별 총 매출
            Long totalSales = supplierRepository.calculateTotalSalesBySupplierId(supplier.getId());
            dto.setTotalSales(totalSales != null ? totalSales : 0L);
        } catch (Exception e) {
            log.error("공급사 추가 정보 조회 중 오류 발생: {}", e.getMessage());
        }

        return dto;
    }

    private ProductDTO convertToProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .brand(product.getBrand())
                .category(product.getCategory())
                .supplier(product.getSupplier())
                .mainImage(product.getMainImage())
                .images(product.getImages())
                .stock(product.getStock())
                .status(product.getStatus())
                .isActive(product.getIsActive())
                .isFeatured(product.getIsFeatured())
                .averageRating(product.getAverageRating())
                .reviewCount(product.getReviewCount())
                .salesCount(product.getSalesCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    private Supplier convertToEntity(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        updateSupplierFromDTO(supplier, dto);
        return supplier;
    }

    private void updateSupplierFromDTO(Supplier supplier, SupplierDTO dto) {
        if (dto.getCode() != null) {
            supplier.setCode(dto.getCode());
        }
        if (dto.getName() != null) {
            supplier.setName(dto.getName());
        }
        if (dto.getBusinessNumber() != null) {
            supplier.setBusinessNumber(dto.getBusinessNumber());
        }
        if (dto.getRepresentativeName() != null) {
            supplier.setRepresentativeName(dto.getRepresentativeName());
        }
        if (dto.getEmail() != null) {
            supplier.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            supplier.setPhone(dto.getPhone());
        }
        if (dto.getAddress1() != null) {
            supplier.setAddress1(dto.getAddress1());
        }
        if (dto.getAddress2() != null) {
            supplier.setAddress2(dto.getAddress2());
        }
        if (dto.getBankName() != null) {
            supplier.setBankName(dto.getBankName());
        }
        if (dto.getAccountNumber() != null) {
            supplier.setAccountNumber(dto.getAccountNumber());
        }
        if (dto.getAccountHolder() != null) {
            supplier.setAccountHolder(dto.getAccountHolder());
        }
        if (dto.getCity() != null) {
            supplier.setCity(dto.getCity());
        }
        if (dto.getState() != null) {
            supplier.setState(dto.getState());
        }
        if (dto.getZipCode() != null) {
            supplier.setZipCode(dto.getZipCode());
        }
        if (dto.getCountry() != null) {
            supplier.setCountry(dto.getCountry());
        }
        if (dto.getWebsite() != null) {
            supplier.setWebsite(dto.getWebsite());
        }
        if (dto.getDescription() != null) {
            supplier.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            supplier.setStatus(dto.getStatus());
        }
    }

    // 공급업체 코드 생성 메서드
    private String generateSupplierCode(String name) {
        // 이름에서 첫 3글자를 대문자로 변환하고 현재 시간의 밀리초를 추가
        String prefix = name.length() >= 3 ? name.substring(0, 3).toUpperCase() : name.toUpperCase();
        String suffix = String.valueOf(System.currentTimeMillis() % 10000);
        return prefix + "-" + suffix;
    }

    // 총 매출 계산 메서드
    private Long calculateTotalSales() {
        try {
            List<Object[]> salesData = supplierRepository.calculateTotalSalesBySupplier();
            if (salesData == null || salesData.isEmpty()) {
                return 0L;
            }

            return salesData.stream()
                    .mapToLong(data -> {
                        if (data[1] == null) return 0L;
                        return ((Number) data[1]).longValue();
                    })
                    .sum();
        } catch (Exception e) {
            log.error("총 매출 계산 중 오류 발생: {}", e.getMessage());
            return 0L;
        }
    }
}