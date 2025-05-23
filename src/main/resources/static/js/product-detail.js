// 상품 상세 정보를 가져오는 함수
async function fetchProductDetail(productId) {
    try {
        const response = await fetch(`/api/products/${productId}`, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        if (!response.ok) {
            throw new Error('상품 정보를 가져오는데 실패했습니다.');
        }
        return await response.json();
    } catch (error) {
        handleError(error, '상품 정보를 불러오는데 실패했습니다.');
        return null;
    }
}

// 관련 상품 목록을 가져오는 함수
async function fetchRelatedProducts(productId, categoryId, limit = 4) {
    try {
        const response = await fetch(`/api/products/category/${categoryId}?size=${limit}`, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        if (!response.ok) {
            throw new Error('관련 상품을 불러오는데 실패했습니다.');
        }
        const data = await response.json();
        // 현재 상품을 제외한 상품들만 반환
        return data.content.filter(product => product.id !== productId);
    } catch (error) {
        handleError(error, '관련 상품을 불러오는데 실패했습니다.');
        return [];
    }
}

// 상품 리뷰 목록을 가져오는 함수
async function fetchProductReviews(productId, page = 0, size = 5) {
    try {
        const response = await fetch(`/api/reviews/product/${productId}?page=${page}&size=${size}`, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        if (!response.ok) {
            throw new Error('상품 리뷰를 불러오는데 실패했습니다.');
        }
        return await response.json();
    } catch (error) {
        handleError(error, '상품 리뷰를 불러오는데 실패했습니다.');
        return { content: [], totalElements: 0, totalPages: 0 };
    }
}

// 상품 조회수 증가 함수
async function trackProductView(productId) {
    try {
        await fetch(`/api/products/${productId}/view`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
    } catch (error) {
        console.error('상품 조회수 증가 실패:', error);
    }
}

// 에러 처리 함수
function handleError(error, message) {
    console.error(error);
    
    // 토스트 메시지 생성
    const toast = document.createElement('div');
    toast.className = 'toast align-items-center text-white bg-danger border-0 position-fixed bottom-0 end-0 m-3';
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
    `;
    document.body.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
    
    // 토스트 메시지 자동 제거
    toast.addEventListener('hidden.bs.toast', () => {
        toast.remove();
    });
}

// 별점을 렌더링하는 함수
function renderRatingStars(rating) {
    let starsHTML = '';
    
    // 꽉 찬 별
    for (let i = 1; i <= Math.floor(rating); i++) {
        starsHTML += '<i class="fas fa-star"></i>';
    }
    
    // 반 별
    if (rating % 1 >= 0.5) {
        starsHTML += '<i class="fas fa-star-half-alt"></i>';
    }
    
    // 빈 별
    const emptyStars = 5 - Math.floor(rating) - (rating % 1 >= 0.5 ? 1 : 0);
    for (let i = 1; i <= emptyStars; i++) {
        starsHTML += '<i class="far fa-star"></i>';
    }
    
    return starsHTML;
}

// 상품 이미지 갤러리 렌더링
function renderProductGallery(product) {
    const mainImage = document.getElementById('mainProductImage');
    const thumbnailsContainer = document.getElementById('productThumbnails');
    
    if (!mainImage || !thumbnailsContainer) return;

    // 기본 이미지 URL
    const DEFAULT_PRODUCT_IMAGE = '/images/default-product.svg';

    // 메인 이미지 설정
    mainImage.src = DEFAULT_PRODUCT_IMAGE; // 먼저 기본 이미지 표시
    mainImage.alt = product.name || '상품 이미지';

    if (product.images && product.images.length > 0) {
        const firstImage = new Image();
        firstImage.onload = () => {
            mainImage.src = firstImage.src;
        };
        firstImage.onerror = () => {
            mainImage.src = DEFAULT_PRODUCT_IMAGE;
        };
        firstImage.src = product.images[0].imageUrl;
    }

    // 썸네일 이미지 설정
    thumbnailsContainer.innerHTML = '';
    if (product.images && product.images.length > 0) {
        product.images.forEach((image, index) => {
            const thumbnail = document.createElement('div');
            thumbnail.className = `product-thumbnail ${index === 0 ? 'active' : ''}`;
            thumbnail.setAttribute('data-image', image.imageUrl);
            
            const img = document.createElement('img');
            img.src = DEFAULT_PRODUCT_IMAGE; // 먼저 기본 이미지 표시
            img.alt = `${product.name || '상품'} - 이미지 ${index + 1}`;
            
            // 실제 이미지 로드
            const productImage = new Image();
            productImage.onload = () => {
                img.src = productImage.src;
            };
            productImage.onerror = () => {
                img.src = DEFAULT_PRODUCT_IMAGE;
            };
            productImage.src = image.imageUrl;
            
            thumbnail.appendChild(img);
            thumbnail.addEventListener('click', () => {
                mainImage.src = image.imageUrl;
                document.querySelectorAll('.product-thumbnail').forEach(thumb => thumb.classList.remove('active'));
                thumbnail.classList.add('active');
            });
            
            thumbnailsContainer.appendChild(thumbnail);
        });
    } else {
        // 이미지가 없는 경우 기본 이미지 썸네일 추가
        const thumbnail = document.createElement('div');
        thumbnail.className = 'product-thumbnail active';
        thumbnail.setAttribute('data-image', DEFAULT_PRODUCT_IMAGE);
        
        const img = document.createElement('img');
        img.src = DEFAULT_PRODUCT_IMAGE;
        img.alt = '상품 이미지';
        
        thumbnail.appendChild(img);
        thumbnailsContainer.appendChild(thumbnail);
    }
}

// 색상 옵션 렌더링
function renderColorOptions(colors) {
    const colorContainer = document.getElementById('productColors');
    if (!colorContainer) return;
    
    if (!colors || colors.length === 0) {
        colorContainer.innerHTML = '<div class="no-options">색상 옵션이 없습니다.</div>';
        return;
    }

    let html = '';
    colors.forEach(color => {
        const isDisabled = !color.enabled || color.stock <= 0;
        const colorClass = isDisabled ? 'color-option disabled' : 'color-option';
        const style = color.value.startsWith('#') ? `background-color: ${color.value}` : '';
        
        html += `
            <div class="${colorClass}" 
                 data-color-id="${color.id}"
                 data-option-id="${color.optionId}"
                 data-stock="${color.stock}"
                 style="${style}"
                 ${isDisabled ? 'title="품절"' : ''}>
                ${!color.value.startsWith('#') ? color.value : ''}
            </div>
        `;
    });

    colorContainer.innerHTML = html;

    // 색상 선택 이벤트 리스너
    colorContainer.querySelectorAll('.color-option:not(.disabled)').forEach(option => {
        option.addEventListener('click', () => {
            // 이전 선택 제거
            colorContainer.querySelectorAll('.color-option').forEach(opt => opt.classList.remove('active'));
            // 현재 선택 활성화
            option.classList.add('active');
            // 사이즈 옵션 업데이트
            updateSizeOptions();
        });
    });
}

// 사이즈 옵션 렌더링
function renderSizeOptions(sizes) {
    const sizeContainer = document.getElementById('productSizes');
    if (!sizeContainer) return;
    
    if (!sizes || sizes.length === 0) {
        sizeContainer.innerHTML = '<div class="no-options">사이즈 옵션이 없습니다.</div>';
        return;
    }

    let html = '';
    sizes.forEach(size => {
        const isDisabled = !size.enabled || size.stock <= 0;
        const sizeClass = isDisabled ? 'size-option disabled' : 'size-option';
        
        html += `
            <div class="${sizeClass}"
                 data-size-id="${size.id}"
                 data-option-id="${size.optionId}"
                 data-stock="${size.stock}"
                 ${isDisabled ? 'title="품절"' : ''}>
                ${size.value}
            </div>
        `;
    });

    sizeContainer.innerHTML = html;

    // 사이즈 선택 이벤트 리스너
    sizeContainer.querySelectorAll('.size-option:not(.disabled)').forEach(option => {
        option.addEventListener('click', () => {
            // 이전 선택 제거
            sizeContainer.querySelectorAll('.size-option').forEach(opt => opt.classList.remove('active'));
            // 현재 선택 활성화
            option.classList.add('active');
        });
    });
}

// 선택된 색상에 따른 사이즈 옵션 업데이트
function updateSizeOptions() {
    const selectedColor = document.querySelector('.color-option.active');
    if (!selectedColor) return;

    const colorId = selectedColor.dataset.colorId;
    const sizeContainer = document.getElementById('productSizes');
    const sizes = window.productData.sizeOptions.filter(size => 
        size.optionId === selectedColor.dataset.optionId && size.stock > 0
    );

    renderSizeOptions(sizes);
}

// 수량 증가 함수
function increaseQuantity() {
    const quantityInput = document.getElementById('productQuantity');
    const currentValue = parseInt(quantityInput.value);
    const maxValue = parseInt(quantityInput.getAttribute('max'));
    
    if (currentValue < maxValue) {
        quantityInput.value = currentValue + 1;
    }
}

// 수량 감소 함수
function decreaseQuantity() {
    const quantityInput = document.getElementById('productQuantity');
    const currentValue = parseInt(quantityInput.value);
    const minValue = parseInt(quantityInput.getAttribute('min'));
    
    if (currentValue > minValue) {
        quantityInput.value = currentValue - 1;
    }
}

// 장바구니 아이콘 업데이트 함수
async function updateCartIcon() {
    try {
        const response = await fetch('/api/cart/count');
        if (response.ok) {
            const count = await response.json();
            const cartIcon = document.getElementById('cartCount');
            if (cartIcon) {
                cartIcon.textContent = count;
            }
        }
    } catch (error) {
        console.error('장바구니 아이콘 업데이트 실패:', error);
    }
}

// 위시리스트 아이콘 업데이트 함수
async function updateWishlistIcon() {
    try {
        const response = await fetch('/api/wishlist/count', {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        
        // 인증되지 않은 경우 처리
        if (response.status === 401 || response.status === 403) {
            const wishlistIcon = document.getElementById('wishlistCount');
            if (wishlistIcon) {
                wishlistIcon.textContent = '0';
            }
            return;
        }
        
        if (!response.ok) {
            throw new Error('위시리스트 개수를 가져오는데 실패했습니다.');
        }
        
        const count = await response.json();
        const wishlistIcon = document.getElementById('wishlistCount');
        if (wishlistIcon) {
            wishlistIcon.textContent = count;
        }
    } catch (error) {
        console.error('위시리스트 아이콘 업데이트 실패:', error);
        // 에러 발생 시 아이콘을 0으로 설정
        const wishlistIcon = document.getElementById('wishlistCount');
        if (wishlistIcon) {
            wishlistIcon.textContent = '0';
        }
    }
}

// 장바구니 추가 함수
async function addToCart() {
    const colorOption = document.querySelector('.color-option.active');
    const sizeOption = document.querySelector('.size-option.active');
    const quantity = document.getElementById('productQuantity').value;
    
    if (!colorOption || !sizeOption) {
        handleError(null, '색상과 사이즈를 선택해주세요.');
        return;
    }
    
    try {
        const response = await fetch('/api/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                productId: getProductIdFromUrl(),
                colorId: colorOption.getAttribute('data-color-id'),
                sizeId: sizeOption.getAttribute('data-size-id'),
                quantity: parseInt(quantity)
            })
        });
        
        if (!response.ok) {
            throw new Error('장바구니 추가에 실패했습니다.');
        }
        
        alert('장바구니에 추가되었습니다.');
        // 장바구니 아이콘 업데이트
        await updateCartIcon();
    } catch (error) {
        handleError(error, '장바구니 추가에 실패했습니다.');
    }
}

// 바로구매 함수
async function buyNow() {
    const colorOption = document.querySelector('.color-option.active');
    const sizeOption = document.querySelector('.size-option.active');
    const quantity = document.getElementById('productQuantity').value;
    
    if (!colorOption || !sizeOption) {
        handleError(null, '색상과 사이즈를 선택해주세요.');
        return;
    }
    
    try {
        const response = await fetch('/api/cart', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                productId: getProductIdFromUrl(),
                colorId: colorOption.getAttribute('data-color-id'),
                sizeId: sizeOption.getAttribute('data-size-id'),
                quantity: parseInt(quantity)
            })
        });
        
        if (!response.ok) {
            throw new Error('장바구니 추가에 실패했습니다.');
        }
        
        // 장바구니에 추가 후 주문 페이지로 이동
        window.location.href = '/order';
    } catch (error) {
        handleError(error, '바로구매 처리에 실패했습니다.');
    }
}

// 위시리스트 추가/제거 함수
async function toggleWishlist() {
    const wishButton = document.getElementById('addToWishlistBtn');
    const isActive = wishButton.classList.contains('active');
    const productId = getProductIdFromUrl();
    
    try {
        const response = await fetch(`/api/wishlist/items/${productId}`, {
            method: isActive ? 'DELETE' : 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        
        // 인증되지 않은 경우 로그인 페이지로 리다이렉트
        if (response.status === 401 || response.status === 403) {
            window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname);
            return;
        }
        
        if (!response.ok) {
            throw new Error('위시리스트 업데이트에 실패했습니다.');
        }
        
        // 버튼 상태 업데이트
        wishButton.classList.toggle('active');
        const icon = wishButton.querySelector('i');
        if (icon) {
            icon.classList.toggle('far');
            icon.classList.toggle('fas');
        }
        
        // 위시리스트 아이콘 업데이트
        await updateWishlistIcon();
        
        // 성공 메시지 표시
        const message = isActive ? '위시리스트에서 제거되었습니다.' : '위시리스트에 추가되었습니다.';
        const toast = document.createElement('div');
        toast.className = 'toast align-items-center text-white bg-success border-0 position-fixed bottom-0 end-0 m-3';
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        `;
        document.body.appendChild(toast);
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
        
        // 토스트 메시지 자동 제거
        toast.addEventListener('hidden.bs.toast', () => {
            toast.remove();
        });
    } catch (error) {
        handleError(error, '위시리스트 업데이트에 실패했습니다.');
    }
}

// URL에서 상품 ID 가져오기
function getProductIdFromUrl() {
    const pathParts = window.location.pathname.split('/');
    return pathParts[pathParts.length - 1];
}

// 상품 상세 정보를 화면에 렌더링하는 함수
function renderProductDetail(product) {
    // 상품 데이터를 전역 변수로 저장
    window.productData = product;

    const productNotFound = document.getElementById('productNotFound');
    const productDetail = document.getElementById('productDetail');

    if (!product) {
        if (productNotFound) {
            productNotFound.style.display = 'block';
        }
        if (productDetail) {
            productDetail.style.display = 'none';
        }
        return;
    }

    if (productNotFound) {
        productNotFound.style.display = 'none';
    }
    if (productDetail) {
        productDetail.style.display = 'block';
    }

    // 상품 이미지 갤러리 렌더링
    renderProductGallery(product);

    // 상품 정보 렌더링
    document.querySelector('.product-title').textContent = product.name;
    document.querySelector('.product-brand').textContent = product.brandName;

    // 별점 렌더링
    const ratingContainer = document.getElementById('productRating');
    if (ratingContainer) {
        ratingContainer.innerHTML = `
            ${renderRatingStars(product.averageRating || 0)}
            <span class="review-count">(${product.reviewCount || 0}개 리뷰)</span>
        `;
    }

    // 가격 렌더링
    const priceContainer = document.getElementById('productPrice');
    if (priceContainer) {
        if (product.salePrice && product.salePrice < product.price) {
            priceContainer.innerHTML = `
                <span class="original-price">${product.price.toLocaleString()}원</span>
                <span class="current-price">${product.salePrice.toLocaleString()}원</span>
                <span class="discount-percent">${Math.round((1 - product.salePrice / product.price) * 100)}% 할인</span>
            `;
        } else {
            priceContainer.innerHTML = `
                <span class="current-price">${product.price.toLocaleString()}원</span>
            `;
        }
    }

    // 상품 설명 렌더링
    const descriptionElement = document.getElementById('productDescription');
    if (descriptionElement) {
        descriptionElement.innerHTML = product.description;
    }

    // 상품 SKU, 카테고리, 태그 렌더링
    const skuElement = document.getElementById('productSku');
    if (skuElement) {
        skuElement.textContent = product.sku || 'N/A';
    }

    const categoryElement = document.getElementById('productCategory');
    if (categoryElement) {
        categoryElement.textContent = product.categoryName || 'N/A';
    }

    // 태그 렌더링
    const tagsElement = document.getElementById('productTags');
    if (tagsElement) {
        if (product.tags && product.tags.length > 0) {
            tagsElement.innerHTML = product.tags
                .map(tag => `<a href="/shop?tag=${tag}" class="text-decoration-none me-2">${tag}</a>`)
                .join(', ');
        } else {
            tagsElement.textContent = 'N/A';
        }
    }

    // 재고 상태 렌더링
    const stockElement = document.getElementById('productStock');
    if (stockElement) {
        if (product.stockQuantity > 0) {
            stockElement.innerHTML = `<span class="text-success">재고 있음 (${product.stockQuantity}개)</span>`;
        } else {
            stockElement.innerHTML = '<span class="text-danger">품절</span>';
        }
    }

    // 옵션 렌더링
    if (product.colorOptions && product.colorOptions.length > 0) {
        renderColorOptions(product.colorOptions);
    } else {
        const colorContainer = document.getElementById('productColors');
        if (colorContainer) {
            colorContainer.innerHTML = '<div class="no-options">색상 옵션이 없습니다.</div>';
        }
    }
    
    if (product.sizeOptions && product.sizeOptions.length > 0) {
        renderSizeOptions(product.sizeOptions);
    } else {
        const sizeContainer = document.getElementById('productSizes');
        if (sizeContainer) {
            sizeContainer.innerHTML = '<div class="no-options">사이즈 옵션이 없습니다.</div>';
        }
    }

    // 수량 입력 필드 초기화
    const quantityElement = document.getElementById('productQuantity');
    if (quantityElement) {
        quantityElement.value = 1;
    }

    // 장바구니, 위시리스트 버튼 이벤트 설정
    setupActionButtons(product.id);

    // 관련 상품 렌더링
    if (product.categoryId) {
        fetchRelatedProducts(product.id, product.categoryId)
            .then(relatedProducts => {
                renderRelatedProducts(relatedProducts);
            });
    }

    // 리뷰 렌더링
    fetchProductReviews(product.id)
        .then(reviews => {
            renderProductReviews(reviews);
        });
}

// 장바구니, 위시리스트 버튼 이벤트 설정 함수
function setupActionButtons(productId) {
    // 장바구니 담기 버튼
    const addToCartBtn = document.getElementById('addToCartBtn');
    if (addToCartBtn) {
        addToCartBtn.onclick = addToCart;
    }

    // 바로구매 버튼
    const buyNowBtn = document.getElementById('buyNowBtn');
    if (buyNowBtn) {
        buyNowBtn.onclick = buyNow;
    }

    // 위시리스트 추가/제거 버튼
    const addToWishlistBtn = document.getElementById('addToWishlistBtn');
    if (addToWishlistBtn) {
        addToWishlistBtn.onclick = toggleWishlist;
    }
}

// 관련 상품 렌더링 함수
function renderRelatedProducts(products) {
    const relatedProductsContainer = document.querySelector('.related-products .row');
    if (!relatedProductsContainer) return;

    if (!products || products.length === 0) {
        relatedProductsContainer.innerHTML = `
            <div class="col-12 text-center py-4">
                <p>관련 상품이 없습니다.</p>
            </div>
        `;
        return;
    }

    // 기본 이미지 URL
    const DEFAULT_PRODUCT_IMAGE = '/images/default-product.svg';

    relatedProductsContainer.innerHTML = products.map(product => {
        // 가격 정보 처리
        const price = product.price || 0;
        const originalPrice = product.originalPrice || price;
        const discountRate = product.discountRate || 0;
        
        // 상품 이미지 URL 결정
        let productImageUrl = DEFAULT_PRODUCT_IMAGE;
        if (product.images && product.images.length > 0) {
            productImageUrl = product.images[0].imageUrl;
        } else if (product.mainImage) {
            productImageUrl = product.mainImage;
        }
        
        return `
            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card related-product-card h-100">
                    <div class="product-image-container">
                        <img src="${DEFAULT_PRODUCT_IMAGE}" 
                             class="card-img-top related-product-img" 
                             alt="${product.name || '상품 이미지'}"
                             data-src="${productImageUrl}"
                             onload="this.classList.add('loaded')">
                    </div>
                    <div class="card-body">
                        <h5 class="related-product-title">${product.name || '상품명 없음'}</h5>
                        <p class="related-product-price">
                            ${originalPrice > price ? 
                                `<span class="original-price">₩${originalPrice.toLocaleString()}</span>` : 
                                ''}
                            <span class="current-price">₩${price.toLocaleString()}</span>
                            ${discountRate > 0 ? 
                                `<span class="discount-percent">${discountRate}%</span>` : 
                                ''}
                        </p>
                    </div>
                    <a href="/products/${product.id}" class="stretched-link"></a>
                </div>
            </div>
        `;
    }).join('');

    // 이미지 지연 로딩 처리
    const images = relatedProductsContainer.querySelectorAll('img[data-src]');
    images.forEach(img => {
        const productImage = new Image();
        productImage.onload = () => {
            img.src = productImage.src;
            img.classList.add('loaded');
        };
        productImage.onerror = () => {
            img.src = DEFAULT_PRODUCT_IMAGE;
            img.classList.add('loaded');
        };
        productImage.src = img.dataset.src;
    });
}

// 상품 리뷰 렌더링 함수
function renderProductReviews(reviews) {
    const reviewsContainer = document.querySelector('#reviews .card');
    if (!reviewsContainer) return;

    if (!reviews.content || reviews.content.length === 0) {
        reviewsContainer.innerHTML = `
            <div class="text-center py-4">
                <i class="far fa-comment-dots fs-1 text-muted mb-3"></i>
                <p>아직 리뷰가 없습니다. 첫 번째 리뷰를 작성해보세요.</p>
            </div>
        `;
        return;
    }

    reviewsContainer.innerHTML = reviews.content.map(review => `
        <div class="card review-card mb-3">
            <div class="card-body">
                <div class="review-header">
                    <div class="reviewer-info">
                        <div class="reviewer-avatar">
                            <i class="fas fa-user"></i>
                        </div>
                        <div>
                            <h6 class="mb-0">${review.memberName}</h6>
                            <div class="review-rating">
                                ${renderRatingStars(review.rating)}
                            </div>
                            <small class="review-date">${new Date(review.createdDate).toLocaleDateString()}</small>
                        </div>
                    </div>
                </div>
                
                <h5 class="card-title">${review.title}</h5>
                <p class="card-text">${review.content}</p>
                
                ${review.images && review.images.length > 0 ? `
                    <div class="review-images">
                        ${review.images.map(image => `
                            <img src="${image.imageUrl}" class="review-image" alt="리뷰 이미지">
                        `).join('')}
                    </div>
                ` : ''}
            </div>
        </div>
    `).join('');
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', async () => {
    const productId = getProductIdFromUrl();
    if (productId) {
        // 상품 조회수 증가
        await trackProductView(productId);
        
        // 상품 정보 로드
        const product = await fetchProductDetail(productId);
        if (product) {
            renderProductDetail(product);
        }
    }
    
    // 장바구니, 위시리스트 아이콘 업데이트
    updateCartIcon();
    updateWishlistIcon();
});

