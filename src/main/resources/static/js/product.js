// 상품 목록을 가져오는 함수
async function fetchProducts(page = 1, size = 12, sort = "newest") {
    try {
        // API 엔드포인트에 요청
        const response = await fetch(`/api/products?page=${page - 1}&size=${size}&sort=${sort}`)

        if (!response.ok) {
            throw new Error("상품을 불러오는데 실패했습니다.")
        }

        return await response.json()
    } catch (error) {
        console.error("상품 목록 조회 오류:", error)
        return { content: [], totalElements: 0, totalPages: 0 }
    }
}

// 상품 목록을 화면에 렌더링하는 함수
function renderProducts(products) {
    const productGrid = document.getElementById("productGrid")

    // 상품 목록이 비어있는 경우
    if (!products || products.length === 0) {
        document.querySelector(".empty-results").style.display = "block"
        productGrid.innerHTML = ""
        return
    }

    document.querySelector(".empty-results").style.display = "none"

    // 상품 목록 HTML 생성
    let productsHTML = ""

    products.forEach((product) => {
        productsHTML += `
            <div class="col-md-6 col-lg-4">
                <div class="product-card">
                    <div class="product-img-container">
                        ${product.new ? '<span class="product-badge badge-new">NEW</span>' : ""}
                        ${product.discountRate > 0 ? `<span class="product-badge badge-sale">${product.discountRate}% OFF</span>` : ""}
                        ${product.hot ? '<span class="product-badge badge-hot">HOT</span>' : ""}
                        
                        <img src="${product.imageUrl || "/placeholder.svg?height=250&width=250"}" class="product-img" alt="${product.name}">
                        
                        <div class="product-actions">
                            <button class="product-action-btn" title="장바구니에 담기" onclick="addToCart(${product.id})">
                                <i class="fas fa-shopping-cart"></i>
                            </button>
                            <button class="product-action-btn" title="위시리스트에 추가" onclick="addToWishlist(${product.id})">
                                <i class="fas fa-heart"></i>
                            </button>
                            <button class="product-action-btn" title="빠른 보기" onclick="quickView(${product.id})">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                    </div>
                    <div class="card-body">
                        <h5 class="product-title">
                            <a href="/products/${product.id}" class="text-decoration-none text-dark">${product.name}</a>
                        </h5>
                        <div class="product-rating">
                            ${renderRatingStars(product.rating)}
                            <span class="ms-1 text-muted">(${product.rating})</span>
                        </div>
                        <div class="product-price">
                            ${
            product.originalPrice > product.price
                ? `<span class="original-price">₩${product.originalPrice.toLocaleString()}</span>`
                : ""
        }
                            <span>₩${product.price.toLocaleString()}</span>
                        </div>
                    </div>
                </div>
            </div>
        `
    })

    productGrid.innerHTML = productsHTML
}

// 별점을 렌더링하는 함수
function renderRatingStars(rating) {
    let starsHTML = ""

    // 꽉 찬 별
    for (let i = 1; i <= Math.floor(rating); i++) {
        starsHTML += '<i class="fas fa-star"></i>'
    }

    // 반 별
    if (rating % 1 >= 0.5) {
        starsHTML += '<i class="fas fa-star-half-alt"></i>'
    }

    // 빈 별
    const emptyStars = 5 - Math.floor(rating) - (rating % 1 >= 0.5 ? 1 : 0)
    for (let i = 1; i <= emptyStars; i++) {
        starsHTML += '<i class="far fa-star"></i>'
    }

    return starsHTML
}

// 페이지네이션을 렌더링하는 함수
function renderPagination(currentPage, totalPages) {
    const pagination = document.querySelector(".pagination")

    let paginationHTML = ""

    // 이전 버튼
    paginationHTML += `
        <li class="page-item ${currentPage <= 1 ? "disabled" : ""}">
            <a class="page-link" href="#" onclick="${currentPage > 1 ? `loadPage(${currentPage - 1})` : ""}" tabindex="${currentPage <= 1 ? "-1" : "0"}" aria-disabled="${currentPage <= 1 ? "true" : "false"}">이전</a>
        </li>
    `

    // 페이지 번호
    const startPage = Math.max(1, currentPage - 2)
    const endPage = Math.min(totalPages, startPage + 4)

    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
            <li class="page-item ${i === currentPage ? "active" : ""}">
                <a class="page-link" href="#" onclick="loadPage(${i})">${i}</a>
            </li>
        `
    }

    // 다음 버튼
    paginationHTML += `
        <li class="page-item ${currentPage >= totalPages ? "disabled" : ""}">
            <a class="page-link" href="#" onclick="${currentPage < totalPages ? `loadPage(${currentPage + 1})` : ""}" tabindex="${currentPage >= totalPages ? "-1" : "0"}" aria-disabled="${currentPage >= totalPages ? "true" : "false"}">다음</a>
        </li>
    `

    pagination.innerHTML = paginationHTML
}

// 페이지 로드 함수
async function loadPage(page = 1) {
    const sortOrder = document.getElementById("sortOrder").value
    const productsData = await fetchProducts(page, 12, sortOrder)

    renderProducts(productsData.content)
    renderPagination(page, productsData.totalPages)

    // 총 상품 수 업데이트
    const totalProductsElement = document.querySelector('span[id="totalProducts"]')
    if (totalProductsElement) {
        totalProductsElement.textContent = `총 ${productsData.totalElements}개 상품`
    }

    // 현재 페이지로 스크롤
    window.scrollTo({ top: 0, behavior: "smooth" })
}

// 정렬 변경 이벤트 핸들러
function handleSortChange() {
    loadPage(1)
}

// 필터 적용 함수
function applyFilters() {
    // 여기에 필터 로직 구현
    // 카테고리, 가격, 색상, 사이즈, 브랜드 등의 필터 값을 가져와서 API 요청에 포함
    loadPage(1)
}

// 필터 초기화 함수
function resetFilters() {
    // 체크박스 초기화
    document.querySelectorAll('input[type="checkbox"]').forEach((checkbox) => {
        checkbox.checked = false
    })

    // 가격 범위 초기화
    const priceRange = document.getElementById("priceRange")
    const priceRangeValue = document.getElementById("priceRangeValue")
    priceRange.value = 200000
    priceRangeValue.textContent = "₩200,000"

    // 색상 선택 초기화
    document.querySelectorAll(".color-option").forEach((option) => option.classList.remove("active"))

    // 정렬 옵션 초기화
    document.getElementById("sortOrder").value = "newest"

    // 필터 적용
    applyFilters()
}

// 장바구니에 상품 추가 함수
function addToCart(productId, quantity = 1) {
    console.log(`상품 ID: ${productId}, 수량: ${quantity}를 장바구니에 추가합니다.`)

    // API 호출
    fetch("/api/cart/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            productId: productId,
            quantity: quantity,
        }),
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("장바구니 추가 실패")
            }
            return response.json()
        })
        .then((data) => {
            // 성공 메시지 표시
            alert("상품이 장바구니에 추가되었습니다.")
        })
        .catch((error) => {
            console.error("장바구니 추가 오류:", error)
            alert("장바구니에 추가하는데 실패했습니다.")
        })
}

// 위시리스트에 상품 추가 함수
function addToWishlist(productId) {
    console.log(`상품 ID: ${productId}를 위시리스트에 추가합니다.`)

    // API 호출
    fetch("/api/wishlist/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            productId: productId,
        }),
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("위시리스트 추가 실패")
            }
            return response.json()
        })
        .then((data) => {
            // 성공 메시지 표시
            alert("상품이 위시리스트에 추가되었습니다.")
        })
        .catch((error) => {
            console.error("위시리스트 추가 오류:", error)
            alert("위시리스트에 추가하는데 실패했습니다.")
        })
}

// 모달 인스턴스 생성
let quickViewModal;

document.addEventListener('DOMContentLoaded', function() {
    // 퀵뷰 모달
    const quickViewModalElement = document.getElementById('quickViewModal');
    quickViewModal = new bootstrap.Modal(quickViewModalElement, {
        backdrop: 'static',
        keyboard: false
    });
});

// 퀵뷰 모달 표시
function showQuickView(productId) {
    fetch(`/api/products/${productId}`)
    .then(response => {
        if (!response.ok) throw new Error('상품 정보를 가져오는데 실패했습니다.');
        return response.json();
    })
    .then(data => {
        document.getElementById('quickViewTitle').textContent = data.name;
        document.getElementById('quickViewPrice').textContent = data.price.toLocaleString() + '원';
        document.getElementById('quickViewDescription').textContent = data.description;
        
        const imageContainer = document.getElementById('quickViewImages');
        imageContainer.innerHTML = '';
        data.images.forEach(image => {
            const img = document.createElement('img');
            img.src = image;
            img.alt = data.name;
            img.className = 'img-fluid';
            imageContainer.appendChild(img);
        });
        
        quickViewModal.show();
    })
    .catch(error => {
        alert(error.message);
    });
}

// 페이지 로드 시 실행
document.addEventListener("DOMContentLoaded", () => {
    // 초기 상품 목록 로드
    loadPage(1)

    // 정렬 옵션 변경 이벤트 리스너
    const sortOrderSelect = document.getElementById("sortOrder")
    if (sortOrderSelect) {
        sortOrderSelect.addEventListener("change", handleSortChange)
    }

    // 필터 초기화 버튼 이벤트 리스너
    const resetFiltersBtn = document.getElementById("resetFilters")
    if (resetFiltersBtn) {
        resetFiltersBtn.addEventListener("click", resetFilters)
    }

    // 가격 범위 슬라이더 이벤트 리스너
    const priceRange = document.getElementById("priceRange")
    const priceRangeValue = document.getElementById("priceRangeValue")

    if (priceRange && priceRangeValue) {
        priceRange.addEventListener("input", function () {
            priceRangeValue.textContent = "₩" + Number.parseInt(this.value).toLocaleString()
        })
    }

    // 필터 적용 버튼 이벤트 리스너
    const applyFilterBtn = document.querySelector(".filter-card button.btn-primary")
    if (applyFilterBtn) {
        applyFilterBtn.addEventListener("click", applyFilters)
    }

    // 색상 옵션 선택 이벤트 리스너
    const colorOptions = document.querySelectorAll(".color-option")
    colorOptions.forEach((option) => {
        option.addEventListener("click", function () {
            // 이미 선택된 경우 선택 해제
            if (this.classList.contains("active")) {
                this.classList.remove("active")
            } else {
                // 다른 옵션 선택 해제
                colorOptions.forEach((opt) => opt.classList.remove("active"))
                // 현재 옵션 선택
                this.classList.add("active")
            }
        })
    })

    // 수량 증가/감소 버튼 이벤트
    const decreaseBtn = document.getElementById("quickViewDecreaseQuantity")
    const increaseBtn = document.getElementById("quickViewIncreaseQuantity")
    const quantityInput = document.getElementById("quickViewQuantity")

    if (decreaseBtn && increaseBtn && quantityInput) {
        decreaseBtn.onclick = () => {
            const quantity = Number.parseInt(quantityInput.value)
            if (quantity > 1) {
                quantityInput.value = quantity - 1
            }
        }

        increaseBtn.onclick = () => {
            const quantity = Number.parseInt(quantityInput.value)
            if (quantity < 10) {
                quantityInput.value = quantity + 1
            }
        }
    }
})

