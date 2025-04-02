// 상품 상세 정보를 가져오는 함수
async function fetchProductDetail(productId) {
    try {
        const response = await fetch(`/api/products/${productId}`)

        if (!response.ok) {
            throw new Error("상품 정보를 불러오는데 실패했습니다.")
        }

        return await response.json()
    } catch (error) {
        console.error("상품 상세 정보 조회 오류:", error)
        return null
    }
}

// 관련 상품 목록을 가져오는 함수
async function fetchRelatedProducts(productId, categoryId, limit = 4) {
    try {
        const response = await fetch(`/api/products/related?productId=${productId}&categoryId=${categoryId}&limit=${limit}`)

        if (!response.ok) {
            throw new Error("관련 상품을 불러오는데 실패했습니다.")
        }

        return await response.json()
    } catch (error) {
        console.error("관련 상품 조회 오류:", error)
        return []
    }
}

// 상품 리뷰 목록을 가져오는 함수
async function fetchProductReviews(productId, page = 0, size = 5) {
    try {
        const response = await fetch(`/api/reviews/product/${productId}?page=${page}&size=${size}`)

        if (!response.ok) {
            throw new Error("상품 리뷰를 불러오는데 실패했습니다.")
        }

        return await response.json()
    } catch (error) {
        console.error("상품 리뷰 조회 오류:", error)
        return { content: [], totalElements: 0, totalPages: 0 }
    }
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

// 상품 상세 정보를 화면에 렌더링하는 함수
function renderProductDetail(product) {
    if (!product) {
        document.getElementById("productNotFound").style.display = "block"
        document.getElementById("productDetail").style.display = "none"
        return
    }

    document.getElementById("productNotFound").style.display = "none"
    document.getElementById("productDetail").style.display = "block"

    // 상품 이미지 갤러리 렌더링
    renderProductGallery(product)

    // 상품 정보 렌더링
    document.getElementById("productName").textContent = product.name

    // 별점 렌더링
    const ratingElement = document.getElementById("productRating")
    ratingElement.innerHTML = `
        ${renderRatingStars(product.rating)}
        <span class="ms-2 text-muted">(${product.reviewCount || 0} 리뷰)</span>
    `

    // 가격 렌더링
    const priceElement = document.getElementById("productPrice")
    if (product.originalPrice > product.price) {
        priceElement.innerHTML = `
            <span class="original-price">₩${product.originalPrice.toLocaleString()}</span>
            <span class="current-price">₩${product.price.toLocaleString()}</span>
        `
    } else {
        priceElement.innerHTML = `<span class="current-price">₩${product.price.toLocaleString()}</span>`
    }

    // 상품 설명 렌더링
    document.getElementById("productDescription").innerHTML = product.description

    // 상품 SKU, 카테고리, 태그 렌더링
    document.getElementById("productSku").textContent = product.sku || "N/A"
    document.getElementById("productCategory").textContent = product.categoryName || "N/A"

    // 태그 렌더링
    const tagsElement = document.getElementById("productTags")
    if (product.tags && product.tags.length > 0) {
        tagsElement.innerHTML = product.tags
            .map((tag) => `<a href="/shop?tag=${tag}" class="text-decoration-none me-2">${tag}</a>`)
            .join(", ")
    } else {
        tagsElement.textContent = "N/A"
    }

    // 재고 상태 렌더링
    const stockElement = document.getElementById("productStock")
    if (product.stockQuantity > 0) {
        stockElement.innerHTML = `<span class="text-success">재고 있음 (${product.stockQuantity}개)</span>`
    } else {
        stockElement.innerHTML = '<span class="text-danger">품절</span>'
    }

    // 색상 옵션 렌더링
    renderColorOptions(product.colors || [])

    // 사이즈 옵션 렌더링
    renderSizeOptions(product.sizes || [])

    // 수량 입력 필드 초기화
    document.getElementById("productQuantity").value = 1

    // 장바구니, 위시리스트 버튼 이벤트 설정
    setupActionButtons(product.id)
}

// 상품 이미지 갤러리 렌더링 함수
function renderProductGallery(product) {
    const mainImageElement = document.getElementById("mainProductImage")
    const thumbnailsContainer = document.getElementById("productThumbnails")

    // 기본 이미지 URL 또는 플레이스홀더
    const defaultImageUrl = product.imageUrl || "/placeholder.svg?height=600&width=600"

    // 메인 이미지 설정
    mainImageElement.src = defaultImageUrl
    mainImageElement.alt = product.name

    // 썸네일 이미지 렌더링
    if (product.images && product.images.length > 0) {
        // 첫 번째 이미지를 메인 이미지로 설정
        mainImageElement.src = product.images[0].url

        // 썸네일 이미지 렌더링
        thumbnailsContainer.innerHTML = product.images
            .map(
                (image, index) => `
            <div class="product-thumbnail ${index === 0 ? "active" : ""}">
                <img src="${image.url}" alt="${product.name} - 이미지 ${index + 1}" 
                     onclick="changeMainImage(this.src)">
            </div>
        `,
            )
            .join("")
    } else {
        // 이미지가 없는 경우 기본 이미지만 표시
        thumbnailsContainer.innerHTML = `
            <div class="product-thumbnail active">
                <img src="${defaultImageUrl}" alt="${product.name}">
            </div>
        `
    }
}

// 메인 이미지 변경 함수
function changeMainImage(imageUrl) {
    const mainImageElement = document.getElementById("mainProductImage")
    mainImageElement.src = imageUrl

    // 썸네일 활성화 상태 변경
    document.querySelectorAll(".product-thumbnail").forEach((thumbnail) => {
        if (thumbnail.querySelector("img").src === imageUrl) {
            thumbnail.classList.add("active")
        } else {
            thumbnail.classList.remove("active")
        }
    })
}

// 색상 옵션 렌더링 함수
function renderColorOptions(colors) {
    const colorsContainer = document.getElementById("productColors")

    if (!colors || colors.length === 0) {
        colorsContainer.innerHTML = "<p>색상 옵션이 없습니다.</p>"
        return
    }

    colorsContainer.innerHTML = colors
        .map(
            (color) => `
        <div class="color-option" style="background-color: ${color.hexCode}" 
             data-color-id="${color.id}" data-color-name="${color.name}"
             title="${color.name}" onclick="selectColor(this)"></div>
    `,
        )
        .join("")
}

// 색상 선택 함수
function selectColor(element) {
    // 이전 선택 해제
    document.querySelectorAll(".color-option").forEach((option) => {
        option.classList.remove("active")
    })

    // 현재 선택 활성화
    element.classList.add("active")

    // 선택된 색상 ID와 이름 저장
    document.getElementById("selectedColorId").value = element.dataset.colorId
    document.getElementById("selectedColorName").value = element.dataset.colorName
}

// 사이즈 옵션 렌더링 함수
function renderSizeOptions(sizes) {
    const sizesContainer = document.getElementById("productSizes")

    if (!sizes || sizes.length === 0) {
        sizesContainer.innerHTML = "<p>사이즈 옵션이 없습니다.</p>"
        return
    }

    sizesContainer.innerHTML = sizes
        .map(
            (size) => `
        <div class="size-option" data-size-id="${size.id}" data-size-name="${size.name}"
             onclick="selectSize(this)">${size.name}</div>
    `,
        )
        .join("")
}

// 사이즈 선택 함수
function selectSize(element) {
    // 이전 선택 해제
    document.querySelectorAll(".size-option").forEach((option) => {
        option.classList.remove("active")
    })

    // 현재 선택 활성화
    element.classList.add("active")

    // 선택된 사이즈 ID와 이름 저장
    document.getElementById("selectedSizeId").value = element.dataset.sizeId
    document.getElementById("selectedSizeName").value = element.dataset.sizeName
}

// 수량 증가 함수
function increaseQuantity() {
    const quantityInput = document.getElementById("productQuantity")
    const currentQuantity = Number.parseInt(quantityInput.value)
    const maxQuantity = Number.parseInt(quantityInput.getAttribute("max")) || 10

    if (currentQuantity < maxQuantity) {
        quantityInput.value = currentQuantity + 1
    }
}

// 수량 감소 함수
function decreaseQuantity() {
    const quantityInput = document.getElementById("productQuantity")
    const currentQuantity = Number.parseInt(quantityInput.value)

    if (currentQuantity > 1) {
        quantityInput.value = currentQuantity - 1
    }
}

// 장바구니, 위시리스트 버튼 이벤트 설정 함수
function setupActionButtons(productId) {
    // 장바구니 담기 버튼
    const addToCartBtn = document.getElementById("addToCartBtn")
    if (addToCartBtn) {
        addToCartBtn.onclick = () => {
            addToCart(productId)
        }
    }

    // 위시리스트 추가 버튼
    const addToWishlistBtn = document.getElementById("addToWishlistBtn")
    if (addToWishlistBtn) {
        addToWishlistBtn.onclick = () => {
            addToWishlist(productId)
        }
    }
}

// 장바구니에 상품 추가 함수
function addToCart(productId) {
    const colorId = document.getElementById("selectedColorId").value
    const sizeId = document.getElementById("selectedSizeId").value
    const quantity = Number.parseInt(document.getElementById("productQuantity").value)

    // 색상과 사이즈 선택 여부 확인
    if (!colorId && document.querySelectorAll(".color-option").length > 0) {
        alert("색상을 선택해주세요.")
        return
    }

    if (!sizeId && document.querySelectorAll(".size-option").length > 0) {
        alert("사이즈를 선택해주세요.")
        return
    }

    console.log(
        `상품 ID: ${productId}, 색상 ID: ${colorId}, 사이즈 ID: ${sizeId}, 수량: ${quantity}를 장바구니에 추가합니다.`,
    )

    // API 호출
    fetch("/api/cart/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            productId: productId,
            colorId: colorId || null,
            sizeId: sizeId || null,
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

            // 장바구니 아이콘 업데이트 (선택 사항)
            updateCartIcon()
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

            // 위시리스트 아이콘 업데이트 (선택 사항)
            updateWishlistIcon()
        })
        .catch((error) => {
            console.error("위시리스트 추가 오류:", error)
            alert("위시리스트에 추가하는데 실패했습니다.")
        })
}

// 장바구니 아이콘 업데이트 함수
function updateCartIcon() {
    // 장바구니 아이콘 업데이트 로직
    // 예: 장바구니 아이템 수 표시
    fetch("/api/cart/count")
        .then((response) => response.json())
        .then((data) => {
            const cartCountElement = document.querySelector(".cart-count")
            if (cartCountElement) {
                cartCountElement.textContent = data.count
                cartCountElement.style.display = data.count > 0 ? "block" : "none"
            }
        })
        .catch((error) => console.error("장바구니 카운트 조회 오류:", error))
}

// 위시리스트 아이콘 업데이트 함수
function updateWishlistIcon() {
    // 위시리스트 아이콘 업데이트 로직
    // 예: 위시리스트 아이템 수 표시
    fetch("/api/wishlist/count")
        .then((response) => response.json())
        .then((data) => {
            const wishlistCountElement = document.querySelector(".wishlist-count")
            if (wishlistCountElement) {
                wishlistCountElement.textContent = data.count
                wishlistCountElement.style.display = data.count > 0 ? "block" : "none"
            }
        })
        .catch((error) => console.error("위시리스트 카운트 조회 오류:", error))
}

// 관련 상품 렌더링 함수
function renderRelatedProducts(products) {
    const relatedProductsContainer = document.getElementById("relatedProducts")

    if (!products || products.length === 0) {
        relatedProductsContainer.innerHTML = '<p class="text-center">관련 상품이 없습니다.</p>'
        return
    }

    relatedProductsContainer.innerHTML = products
        .map(
            (product) => `
        <div class="col-md-6 col-lg-3">
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
    `,
        )
        .join("")
}

// 상품 리뷰 렌더링 함수
function renderProductReviews(reviews, totalReviews, totalPages, currentPage) {
    const reviewsContainer = document.getElementById("productReviews")
    const reviewPagination = document.getElementById("reviewPagination")

    if (!reviews || reviews.length === 0) {
        reviewsContainer.innerHTML = '<p class="text-center">이 상품에 대한 리뷰가 아직 없습니다.</p>'
        reviewPagination.innerHTML = ""
        return
    }

    // 리뷰 목록 렌더링
    reviewsContainer.innerHTML = reviews
        .map(
            (review) => `
        <div class="review-item">
            <div class="review-header">
                <div class="review-user">
                    <img src="${review.userProfileImage || "/placeholder.svg?height=50&width=50"}" alt="${review.userName}" class="review-user-img">
                    <div>
                        <h5 class="review-user-name">${review.userName}</h5>
                        <div class="review-date">${new Date(review.createdAt).toLocaleDateString()}</div>
                    </div>
                </div>
                <div class="review-rating">
                    ${renderRatingStars(review.rating)}
                </div>
            </div>
            <div class="review-content">
                <p>${review.content}</p>
            </div>
            ${
                review.images && review.images.length > 0
                    ? `
                <div class="review-images">
                    ${review.images
                        .map(
                            (image) => `
                        <img src="${image.url}" alt="리뷰 이미지" class="review-image" onclick="showImageModal('${image.url}')">
                    `,
                        )
                        .join("")}
                </div>
            `
                    : ""
            }
        </div>
    `,
        )
        .join("")

    // 페이지네이션 렌더링
    renderReviewPagination(currentPage, totalPages)
}

// 리뷰 페이지네이션 렌더링 함수
function renderReviewPagination(currentPage, totalPages) {
    const paginationElement = document.getElementById("reviewPagination")

    if (totalPages <= 1) {
        paginationElement.innerHTML = ""
        return
    }

    let paginationHTML = ""

    // 이전 버튼
    paginationHTML += `
        <li class="page-item ${currentPage <= 0 ? "disabled" : ""}">
            <a class="page-link" href="#reviews" onclick="${currentPage > 0 ? `loadReviewPage(${currentPage - 1})` : ""}" tabindex="${currentPage <= 0 ? "-1" : "0"}" aria-disabled="${currentPage <= 0 ? "true" : "false"}">이전</a>
        </li>
    `

    // 페이지 번호
    const startPage = Math.max(0, currentPage - 2)
    const endPage = Math.min(totalPages - 1, startPage + 4)

    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
            <li class="page-item ${i === currentPage ? "active" : ""}">
                <a class="page-link" href="#reviews" onclick="loadReviewPage(${i})">${i + 1}</a>
            </li>
        `
    }

    // 다음 버튼
    paginationHTML += `
        <li class="page-item ${currentPage >= totalPages - 1 ? "disabled" : ""}">
            <a class="page-link" href="#reviews" onclick="${currentPage < totalPages - 1 ? `loadReviewPage(${currentPage + 1})` : ""}" tabindex="${currentPage >= totalPages - 1 ? "-1" : "0"}" aria-disabled="${currentPage >= totalPages - 1 ? "true" : "false"}">다음</a>
        </li>
    `

    paginationElement.innerHTML = paginationHTML
}

// 리뷰 페이지 로드 함수
async function loadReviewPage(page) {
    const productId = getProductIdFromUrl()
    const reviewsData = await fetchProductReviews(productId, page)

    renderProductReviews(reviewsData.content, reviewsData.totalElements, reviewsData.totalPages, page)
}

// 이미지 모달 표시 함수
function showImageModal(imageUrl) {
    const modal = document.getElementById("imageModal")
    const modalImg = document.getElementById("modalImage")

    modalImg.src = imageUrl
    const bsModal = new bootstrap.Modal(modal)
    bsModal.show()
}

// URL에서 상품 ID 추출 함수
function getProductIdFromUrl() {
    const pathParts = window.location.pathname.split("/")
    return pathParts[pathParts.length - 1]
}

// 탭 전환 함수
function switchTab(tabId) {
    // 모든 탭 컨텐츠 숨기기
    document.querySelectorAll(".tab-content").forEach((tab) => {
        tab.style.display = "none"
    })

    // 모든 탭 버튼 비활성화
    document.querySelectorAll(".tab-btn").forEach((btn) => {
        btn.classList.remove("active")
    })

    // 선택한 탭 컨텐츠 표시
    document.getElementById(tabId).style.display = "block"

    // 선택한 탭 버튼 활성화
    document.querySelector(`.tab-btn[data-tab="${tabId}"]`).classList.add("active")
}

// 페이지 로드 시 실행
document.addEventListener("DOMContentLoaded", async () => {
    // URL에서 상품 ID 추출
    const productId = getProductIdFromUrl()

    // 상품 상세 정보 로드
    const product = await fetchProductDetail(productId)
    renderProductDetail(product)

    if (product) {
        // 관련 상품 로드
        const relatedProducts = await fetchRelatedProducts(productId, product.categoryId)
        renderRelatedProducts(relatedProducts)

        // 상품 리뷰 로드
        const reviewsData = await fetchProductReviews(productId)
        renderProductReviews(reviewsData.content, reviewsData.totalElements, reviewsData.totalPages, 0)
    }

    // 탭 전환 이벤트 리스너
    document.querySelectorAll(".tab-btn").forEach((btn) => {
        btn.addEventListener("click", function () {
            switchTab(this.dataset.tab)
        })
    })

    // 기본 탭 설정
    switchTab("description")

    // 수량 증가/감소 버튼 이벤트 리스너
    document.getElementById("decreaseQuantity").addEventListener("click", decreaseQuantity)
    document.getElementById("increaseQuantity").addEventListener("click", increaseQuantity)

    // 이미지 모달 닫기 버튼 이벤트 리스너
    document.querySelector("#imageModal .btn-close").addEventListener("click", () => {
        const modal = document.getElementById("imageModal")
        const bsModal = bootstrap.Modal.getInstance(modal)
        if (bsModal) {
            bsModal.hide()
        }
    })
})

