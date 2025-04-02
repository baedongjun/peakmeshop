document.addEventListener("DOMContentLoaded", () => {
    // 장바구니 데이터 로드
    loadCart()

    // 이벤트 리스너 설정
    setupEventListeners()
})

// 장바구니 데이터 로드
function loadCart() {
    fetch("/api/cart")
        .then((response) => {
            if (!response.ok) {
                throw new Error("장바구니를 불러오는데 실패했습니다.")
            }
            return response.json()
        })
        .then((cart) => {
            renderCart(cart)
        })
        .catch((error) => {
            console.error("Error:", error)
            showErrorMessage("장바구니를 불러오는데 실패했습니다. 다시 시도해주세요.")
        })
}

// 장바구니 렌더링
function renderCart(cart) {
    const cartTableBody = document.getElementById("cart-items")
    const cartSummary = document.getElementById("cart-summary")

    // 장바구니가 비어있는 경우
    if (!cart || !cart.items || cart.items.length === 0) {
        document.getElementById("cart-container").innerHTML = `
            <div class="text-center py-5">
                <h3>장바구니가 비어있습니다</h3>
                <p>쇼핑을 계속하시려면 아래 버튼을 클릭하세요.</p>
                <a href="/shop/product-list" class="btn btn-primary">쇼핑 계속하기</a>
            </div>
        `
        return
    }

    // 장바구니 아이템 렌더링
    cartTableBody.innerHTML = ""
    cart.items.forEach((item) => {
        const optionsText = item.options ? item.options.map((opt) => `${opt.name}: ${opt.value}`).join(", ") : ""

        cartTableBody.innerHTML += `
            <tr data-item-id="${item.id}">
                <td class="product-thumbnail">
                    <a href="/shop/product-detail?id=${item.product.id}">
                        <img src="${item.product.thumbnailUrl || "/images/placeholder.jpg"}" alt="${item.product.name}" class="img-fluid">
                    </a>
                </td>
                <td class="product-name">
                    <a href="/shop/product-detail?id=${item.product.id}">${item.product.name}</a>
                    ${optionsText ? `<p class="small text-muted">${optionsText}</p>` : ""}
                </td>
                <td class="product-price">₩${formatPrice(item.price)}</td>
                <td class="product-quantity">
                    <div class="quantity">
                        <button class="qty-btn dec-qty" data-item-id="${item.id}"><i class="fa fa-minus"></i></button>
                        <input type="text" class="qty-input" value="${item.quantity}" min="1" max="99" readonly>
                        <button class="qty-btn inc-qty" data-item-id="${item.id}"><i class="fa fa-plus"></i></button>
                    </div>
                </td>
                <td class="product-subtotal">₩${formatPrice(item.price * item.quantity)}</td>
                <td class="product-remove">
                    <button class="remove-item-btn" data-item-id="${item.id}"><i class="fa fa-times"></i></button>
                </td>
            </tr>
        `
    })

    // 장바구니 요약 정보 렌더링
    cartSummary.innerHTML = `
        <tr>
            <td>소계</td>
            <td>₩${formatPrice(cart.subtotal)}</td>
        </tr>
        ${
        cart.discount > 0
            ? `
        <tr>
            <td>할인</td>
            <td>-₩${formatPrice(cart.discount)}</td>
        </tr>`
            : ""
    }
        <tr>
            <td>총계</td>
            <td>₩${formatPrice(cart.total)}</td>
        </tr>
    `

    // 쿠폰 정보 표시
    const couponSection = document.getElementById("coupon-section")
    if (cart.coupon) {
        couponSection.innerHTML = `
            <div class="applied-coupon">
                <p>적용된 쿠폰: ${cart.coupon.name} (${cart.coupon.discountType === "PERCENTAGE" ? cart.coupon.discountValue + "%" : "₩" + formatPrice(cart.coupon.discountValue)})</p>
                <button id="remove-coupon-btn" class="btn btn-sm btn-outline-danger">쿠폰 제거</button>
            </div>
        `
    } else {
        couponSection.innerHTML = `
            <div class="input-group">
                <input type="text" id="coupon-code" class="form-control" placeholder="쿠폰 코드">
                <button id="apply-coupon-btn" class="btn btn-outline-primary">쿠폰 적용</button>
            </div>
        `
    }
}

// 이벤트 리스너 설정
function setupEventListeners() {
    // 이벤트 위임을 사용하여 동적으로 생성된 요소에 이벤트 연결
    document.addEventListener("click", (event) => {
        // 수량 증가 버튼
        if (event.target.closest(".inc-qty")) {
            const button = event.target.closest(".inc-qty")
            const itemId = button.dataset.itemId
            updateQuantity(itemId, 1)
        }

        // 수량 감소 버튼
        if (event.target.closest(".dec-qty")) {
            const button = event.target.closest(".dec-qty")
            const itemId = button.dataset.itemId
            updateQuantity(itemId, -1)
        }

        // 아이템 제거 버튼
        if (event.target.closest(".remove-item-btn")) {
            const button = event.target.closest(".remove-item-btn")
            const itemId = button.dataset.itemId
            removeItem(itemId)
        }

        // 쿠폰 적용 버튼
        if (event.target.id === "apply-coupon-btn") {
            const couponCode = document.getElementById("coupon-code").value
            applyCoupon(couponCode)
        }

        // 쿠폰 제거 버튼
        if (event.target.id === "remove-coupon-btn") {
            removeCoupon()
        }

        // 장바구니 비우기 버튼
        if (event.target.id === "clear-cart-btn") {
            clearCart()
        }

        // 장바구니 업데이트 버튼
        if (event.target.id === "update-cart-btn") {
            loadCart() // 장바구니 새로고침
        }
    })

    // 체크아웃 폼 제출
    const checkoutForm = document.getElementById("checkout-form")
    if (checkoutForm) {
        checkoutForm.addEventListener("submit", (event) => {
            event.preventDefault()
            window.location.href = "/shop/checkout"
        })
    }
}

// 수량 업데이트
function updateQuantity(itemId, change) {
    const quantityInput = document.querySelector(`tr[data-item-id="${itemId}"] .qty-input`)
    let newQuantity = Number.parseInt(quantityInput.value) + change

    // 최소 1, 최대 99
    newQuantity = Math.max(1, Math.min(99, newQuantity))

    fetch(`/api/cart/items/${itemId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ quantity: newQuantity }),
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("수량 업데이트에 실패했습니다.")
            }
            return response.json()
        })
        .then((cart) => {
            renderCart(cart)
        })
        .catch((error) => {
            console.error("Error:", error)
            showErrorMessage("수량 업데이트에 실패했습니다. 다시 시도해주세요.")
        })
}

// 아이템 제거
function removeItem(itemId) {
    if (!confirm("이 상품을 장바구니에서 제거하시겠습니까?")) {
        return
    }

    fetch(`/api/cart/items/${itemId}`, {
        method: "DELETE",
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("상품 제거에 실패했습니다.")
            }
            return response.json()
        })
        .then((cart) => {
            renderCart(cart)
            showSuccessMessage("상품이 장바구니에서 제거되었습니다.")
        })
        .catch((error) => {
            console.error("Error:", error)
            showErrorMessage("상품 제거에 실패했습니다. 다시 시도해주세요.")
        })
}

// 쿠폰 적용
function applyCoupon(couponCode) {
    if (!couponCode) {
        showErrorMessage("쿠폰 코드를 입력해주세요.")
        return
    }

    fetch(`/api/cart/coupon`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ couponCode: couponCode }),
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("쿠폰 적용에 실패했습니다.")
            }
            return response.json()
        })
        .then((cart) => {
            renderCart(cart)
            showSuccessMessage("쿠폰이 성공적으로 적용되었습니다.")
        })
        .catch((error) => {
            console.error("Error:", error)
            showErrorMessage("쿠폰 적용에 실패했습니다. 유효한 쿠폰 코드인지 확인해주세요.")
        })
}

// 쿠폰 제거
function removeCoupon() {
    fetch(`/api/cart/coupon`, {
        method: "DELETE",
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("쿠폰 제거에 실패했습니다.")
            }
            return response.json()
        })
        .then((cart) => {
            renderCart(cart)
            showSuccessMessage("쿠폰이 제거되었습니다.")
        })
        .catch((error) => {
            console.error("Error:", error)
            showErrorMessage("쿠폰 제거에 실패했습니다. 다시 시도해주세요.")
        })
}

// 장바구니 비우기
function clearCart() {
    if (!confirm("장바구니를 비우시겠습니까?")) {
        return
    }

    fetch(`/api/cart`, {
        method: "DELETE",
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("장바구니 비우기에 실패했습니다.")
            }
            return response.json()
        })
        .then(() => {
            renderCart({ items: [] })
            showSuccessMessage("장바구니가 비워졌습니다.")
        })
        .catch((error) => {
            console.error("Error:", error)
            showErrorMessage("장바구니 비우기에 실패했습니다. 다시 시도해주세요.")
        })
}

// 가격 포맷팅 (1000 -> 1,000)
function formatPrice(price) {
    return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
}

// 성공 메시지 표시
function showSuccessMessage(message) {
    const alertContainer = document.getElementById("alert-container")
    alertContainer.innerHTML = `
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `

    // 3초 후 자동으로 닫기
    setTimeout(() => {
        const alert = document.querySelector(".alert")
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert)
            bsAlert.close()
        }
    }, 3000)
}

// 에러 메시지 표시
function showErrorMessage(message) {
    const alertContainer = document.getElementById("alert-container")
    alertContainer.innerHTML = `
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `

    // 3초 후 자동으로 닫기
    setTimeout(() => {
        const alert = document.querySelector(".alert")
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert)
            bsAlert.close()
        }
    }, 3000)
}

