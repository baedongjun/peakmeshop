package com.peakmeshop.common.exception;

public class InsufficientStockException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Long productId;
    private final String productName;
    private final int requestedQuantity;
    private final int availableQuantity;

    public InsufficientStockException(Long productId, String productName, int requestedQuantity, int availableQuantity) {
        super(String.format("재고 부족: 상품 '%s'(ID: %d)의 요청 수량(%d)이 재고 수량(%d)을 초과합니다.",
                productName, productId, requestedQuantity, availableQuantity));
        this.productId = productId;
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}