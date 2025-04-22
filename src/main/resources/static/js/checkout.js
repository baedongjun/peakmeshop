/**
 * 체크아웃 페이지 API 연동 스크립트
 */

// API 엔드포인트
const API = {
    CART: "/api/cart",
    ADDRESSES: "/api/addresses/member",
    ORDER: "/api/orders",
    COUPON: "/api/coupons/apply",
    SHIPPING: "/api/shipping/calculate",
}

// 체크아웃 관련 데이터
const checkoutData = {
    cart: null,
    addresses: [],
    selectedAddress: null,
    paymentMethod: "creditCard",
    coupon: null,
    shippingMethod: "standard",
    deliveryRequest: "",
    customRequest: "",
    agreeTerms: false,
}

// 페이지 초기화
document.addEventListener("DOMContentLoaded", async () => {
    try {
        // 로딩 오버레이 표시
        showLoading()

        // 장바구니 데이터 로드
        await loadCartData()

        // 저장된 주소 로드
        await loadAddresses()

        // 이벤트 리스너 설정
        setupEventListeners()

        // 주문 요약 업데이트
        updateOrderSummary()

        // 로딩 오버레이 숨김
        hideLoading()
    } catch (error) {
        console.error("체크아웃 페이지 초기화 오류:", error)
        showErrorMessage("페이지 로드 중 오류가 발생했습니다. 다시 시도해주세요.")
        hideLoading()
    }
})

/**
 * 장바구니 데이터 로드
 */
async function loadCartData() {
    try {
        const response = await fetch(API.CART, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
            },
            credentials: "include", // 쿠키 포함
        })

        if (!response.ok) {
            throw new Error("장바구니 데이터를 불러오는데 실패했습니다.")
        }

        const cartData = await response.json()
        checkoutData.cart = cartData

        // 장바구니 상품 목록 렌더링
        renderCartItems(cartData)

        // 주문 요약 업데이트
        updateOrderSummary()

        return cartData
    } catch (error) {
        console.error("장바구니 데이터 로드 오류:", error)
        showErrorMessage("장바구니 정보를 불러오는데 실패했습니다.")
        throw error
    }
}

/**
 * 장바구니 상품 목록 렌더링
 */
function renderCartItems(cartData) {
    const tableBody = document.querySelector(".table tbody")
    if (!tableBody) return

    // 테이블 내용 초기화
    tableBody.innerHTML = ""

    if (!cartData.items || cartData.items.length === 0) {
        const emptyRow = document.createElement("tr")
        emptyRow.innerHTML = `
            <td colspan="4" class="text-center py-4">
                장바구니에 상품이 없습니다. <a href="/shop/products" class="text-decoration-none">쇼핑하러 가기</a>
            </td>
        `
        tableBody.appendChild(emptyRow)
        return
    }

    // 각 상품 행 추가
    cartData.items.forEach((item) => {
        const row = document.createElement("tr")

        // 옵션 정보 생성
        let optionsText = ""
        if (item.options && item.options.length > 0) {
            optionsText = item.options.map((opt) => `${opt.name}: ${opt.value}`).join(", ")
        }

        row.innerHTML = `
            <td>
                <div class="d-flex align-items-center">
                    <img src="${item.product.imageUrl || "/placeholder.svg?height=50&width=50"}" 
                         alt="${item.product.name}" 
                         class="me-3" 
                         style="width: 50px; height: 50px; object-fit: cover; border-radius: 0.25rem;">
                    <div>
                        <span>${item.product.name}</span>
                        ${optionsText ? `<small class="d-block text-muted">${optionsText}</small>` : ""}
                    </div>
                </div>
            </td>
            <td>₩${formatNumber(item.price)}</td>
            <td>${item.quantity}</td>
            <td class="text-end">₩${formatNumber(item.price * item.quantity)}</td>
        `

        tableBody.appendChild(row)
    })
}

/**
 * 저장된 주소 목록 로드
 */
async function loadAddresses() {
    try {
        const response = await fetch(API.ADDRESSES, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
            },
            credentials: "include", // 쿠키 포함
        })

        if (!response.ok) {
            // 비회원이거나 주소가 없는 경우 무시
            if (response.status === 401 || response.status === 404) {
                return []
            }
            throw new Error("주소 정보를 불러오는데 실패했습니다.")
        }

        const addresses = await response.json()
        checkoutData.addresses = addresses

        // 주소 목록 렌더링
        renderAddresses(addresses)

        return addresses
    } catch (error) {
        console.error("주소 정보 로드 오류:", error)
        // 주소 로드 실패는 치명적이지 않으므로 경고만 표시
        showWarningMessage("저장된 주소 정보를 불러오는데 실패했습니다.")
        return []
    }
}

/**
 * 주소 목록 렌더링
 */
function renderAddresses(addresses) {
    const addressSelect = document.getElementById("savedAddress")
    if (!addressSelect) return

    // 기존 옵션 제거 (첫 번째 옵션 제외)
    while (addressSelect.options.length > 1) {
        addressSelect.remove(1)
    }

    // 주소가 없는 경우
    if (!addresses || addresses.length === 0) {
        const option = document.createElement("option")
        option.value = ""
        option.textContent = "저장된 주소가 없습니다"
        option.disabled = true
        addressSelect.appendChild(option)
        return
    }

    // 각 주소 옵션 추가
    addresses.forEach((address) => {
        const option = document.createElement("option")
        option.value = address.id
        option.textContent = `${address.name} (${address.address1} ${address.address2})`
        addressSelect.appendChild(option)

        // 기본 주소인 경우 선택
        if (address.isDefault) {
            option.selected = true
            // 기본 주소 정보 채우기
            fillAddressForm(address)
        }
    })
}

/**
 * 주소 폼 채우기
 */
function fillAddressForm(address) {
    if (!address) return

    document.getElementById("receiverName").value = address.receiverName || ""
    document.getElementById("receiverPhone").value = address.receiverPhone || ""
    document.getElementById("zipcode").value = address.zipcode || ""
    document.getElementById("address1").value = address.address1 || ""
    document.getElementById("address2").value = address.address2 || ""

    // 선택된 주소 저장
    checkoutData.selectedAddress = address
}

/**
 * 배송비 계산
 */
async function calculateShipping() {
    try {
        const zipcode = document.getElementById("zipcode").value
        if (!zipcode) return 3000 // 기본 배송비

        const response = await fetch(`${API.SHIPPING}?zipcode=${zipcode}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
            },
        })

        if (!response.ok) {
            return 3000 // 기본 배송비
        }

        const data = await response.json()
        return data.shippingFee || 3000
    } catch (error) {
        console.error("배송비 계산 오류:", error)
        return 3000 // 기본 배송비
    }
}

/**
 * 주문 요약 업데이트
 */
async function updateOrderSummary() {
    if (!checkoutData.cart) return

    const subtotalElement = document.getElementById("subtotal")
    const shippingElement = document.getElementById("shipping")
    const discountElement = document.getElementById("discount")
    const totalElement = document.getElementById("total")

    // 소계 계산
    const subtotal = checkoutData.cart.subtotal || 0

    // 배송비 계산
    const shippingFee = await calculateShipping()

    // 할인 금액
    const discount = checkoutData.cart.discount || 0

    // 총계 계산
    const total = subtotal + shippingFee - discount

    // 화면 업데이트
    if (subtotalElement) subtotalElement.textContent = `₩${formatNumber(subtotal)}`
    if (shippingElement) shippingElement.textContent = `₩${formatNumber(shippingFee)}`
    if (discountElement) discountElement.textContent = `-₩${formatNumber(discount)}`
    if (totalElement) totalElement.textContent = `₩${formatNumber(total)}`
}

/**
 * 쿠폰 적용
 */
async function applyCoupon(couponCode) {
    try {
        if (!couponCode) {
            showWarningMessage("쿠폰 코드를 입력해주세요.")
            return
        }

        // 로딩 표시
        showLoading()

        const response = await fetch(API.COUPON, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
            },
            body: JSON.stringify({
                cartId: checkoutData.cart.id,
                couponCode: couponCode,
            }),
        })

        if (!response.ok) {
            const errorData = await response.json()
            throw new Error(errorData.message || "쿠폰 적용에 실패했습니다.")
        }

        const data = await response.json()
        checkoutData.cart = data.cart
        checkoutData.coupon = data.coupon

        // 쿠폰 정보 표시
        displayAppliedCoupon(data.coupon)

        // 주문 요약 업데이트
        updateOrderSummary()

        showSuccessMessage("쿠폰이 적용되었습니다.")
        hideLoading()
    } catch (error) {
        console.error("쿠폰 적용 오류:", error)
        showErrorMessage(error.message || "쿠폰 적용에 실패했습니다.")
        hideLoading()
    }
}

/**
 * 적용된 쿠폰 표시
 */
function displayAppliedCoupon(coupon) {
    if (!coupon) return

    const appliedCouponDiv = document.getElementById("appliedCoupon")
    const couponNameElement = document.getElementById("couponName")
    const couponDescriptionElement = document.getElementById("couponDescription")

    if (appliedCouponDiv && couponNameElement && couponDescriptionElement) {
        couponNameElement.textContent = coupon.name
        couponDescriptionElement.textContent = coupon.description || `${coupon.discountAmount}원 할인`
        appliedCouponDiv.classList.remove("d-none")
    }
}

/**
 * 쿠폰 제거
 */
async function removeCoupon() {
    try {
        if (!checkoutData.coupon) return

        // 로딩 표시
        showLoading()

        const response = await fetch(`${API.COUPON}/${checkoutData.cart.id}`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
            },
        })

        if (!response.ok) {
            const errorData = await response.json()
            throw new Error(errorData.message || "쿠폰 제거에 실패했습니다.")
        }

        const data = await response.json()
        checkoutData.cart = data
        checkoutData.coupon = null

        // 쿠폰 정보 숨김
        const appliedCouponDiv = document.getElementById("appliedCoupon")
        if (appliedCouponDiv) {
            appliedCouponDiv.classList.add("d-none")
        }

        // 주문 요약 업데이트
        updateOrderSummary()

        showSuccessMessage("쿠폰이 제거되었습니다.")
        hideLoading()
    } catch (error) {
        console.error("쿠폰 제거 오류:", error)
        showErrorMessage(error.message || "쿠폰 제거에 실패했습니다.")
        hideLoading()
    }
}

/**
 * 주문 처리
 */
async function processOrder(formData) {
    try {
        // 로딩 표시
        showLoading()

        // 주문 데이터 구성
        const orderData = {
            cartId: checkoutData.cart.id,
            shipping: {
                receiverName: formData.get("receiverName"),
                receiverPhone: formData.get("receiverPhone"),
                zipcode: formData.get("zipcode"),
                address1: formData.get("address1"),
                address2: formData.get("address2"),
                deliveryRequest:
                    formData.get("deliveryRequest") === "custom"
                        ? formData.get("customRequest")
                        : formData.get("deliveryRequest"),
            },
            payment: {
                method: formData.get("paymentMethod"),
                // 결제 방법에 따른 추가 정보
                details: getPaymentDetails(formData),
            },
            saveAddress: formData.get("saveAsDefault") === "on",
        }

        // 주문 API 호출
        const response = await fetch(API.ORDER, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Accept: "application/json",
            },
            body: JSON.stringify(orderData),
        })

        if (!response.ok) {
            const errorData = await response.json()
            throw new Error(errorData.message || "주문 처리에 실패했습니다.")
        }

        const data = await response.json()

        // 주문 성공 시 주문 완료 페이지로 이동
        window.location.href = `/shop/order-complete?orderId=${data.id}`
        return true
    } catch (error) {
        console.error("주문 처리 오류:", error)
        showErrorMessage(error.message || "주문 처리 중 오류가 발생했습니다.")
        hideLoading()
        return false
    }
}

/**
 * 결제 방법에 따른 추가 정보 가져오기
 */
function getPaymentDetails(formData) {
    const paymentMethod = formData.get("paymentMethod")

    switch (paymentMethod) {
        case "creditCard":
            return {
                // 실제 구현 시에는 카드 정보를 안전하게 처리해야 함
                // 여기서는 예시로만 작성
                cardNumber: "************1234",
                cardHolder: "홍길동",
                expiryAt: "12/25",
                cvv: "***",
            }
        case "bankTransfer":
            return {
                bankName: "국민은행",
                accountNumber: "123-456-789012",
                accountHolder: "피크미샵",
            }
        case "kakaoPay":
        case "naverPay":
            return {}
        default:
            return {}
    }
}

/**
 * 이벤트 리스너 설정
 */
function setupEventListeners() {
    // 기본 배송지 사용 체크박스
    const useDefaultAddressCheckbox = document.getElementById("useDefaultAddress")
    const addressListDiv = document.getElementById("addressList")
    const addressFormDiv = document.getElementById("addressForm")

    if (useDefaultAddressCheckbox) {
        useDefaultAddressCheckbox.addEventListener("change", function () {
            if (this.checked) {
                addressListDiv.style.display = "none"
                addressFormDiv.style.display = "block"
            } else {
                addressListDiv.style.display = "block"
                addressFormDiv.style.display = "none"
            }
        })
    }

    // 저장된 배송지 선택 이벤트
    const savedAddressSelect = document.getElementById("savedAddress")
    if (savedAddressSelect) {
        savedAddressSelect.addEventListener("change", function () {
            if (this.value) {
                const selectedAddress = checkoutData.addresses.find((addr) => addr.id == this.value)
                if (selectedAddress) {
                    fillAddressForm(selectedAddress)
                    addressFormDiv.style.display = "block"
                }
            } else {
                addressFormDiv.style.display = "none"
            }
        })
    }

    // 주소 찾기 버튼 이벤트
    const searchZipcodeButton = document.getElementById("searchZipcode")
    if (searchZipcodeButton) {
        searchZipcodeButton.addEventListener("click", () => {
            // 실제로는 다음 우편번호 API 등을 사용하여 주소 검색
            alert("주소 검색 기능은 실제 구현 시 다음 우편번호 API 등을 사용하세요.")
        })
    }

    // 배송 요청사항 선택 이벤트
    const deliveryRequestSelect = document.getElementById("deliveryRequest")
    const customRequestDiv = document.getElementById("customRequestDiv")
    if (deliveryRequestSelect && customRequestDiv) {
        deliveryRequestSelect.addEventListener("change", function () {
            if (this.value === "custom") {
                customRequestDiv.style.display = "block"
            } else {
                customRequestDiv.style.display = "none"
            }
        })
    }

    // 결제 방법 선택 이벤트
    const paymentMethodItems = document.querySelectorAll(".payment-method-item")
    paymentMethodItems.forEach((item) => {
        item.addEventListener("click", function () {
            // 라디오 버튼 선택
            const radio = this.querySelector('input[type="radio"]')
            radio.checked = true

            // 활성화 클래스 토글
            paymentMethodItems.forEach((i) => i.classList.remove("active"))
            this.classList.add("active")

            // 결제 방법 저장
            checkoutData.paymentMethod = radio.value
        })
    })

    // 폼 제출 이벤트
    const checkoutForm = document.getElementById("checkoutForm")
    if (checkoutForm) {
        checkoutForm.addEventListener("submit", async (event) => {
            event.preventDefault()

            // 필수 입력 필드 검증
            const requiredFields = checkoutForm.querySelectorAll("[required]")
            let isValid = true

            requiredFields.forEach((field) => {
                if (!field.value.trim()) {
                    isValid = false
                    field.classList.add("is-invalid")
                } else {
                    field.classList.remove("is-invalid")
                }
            })

            if (!isValid) {
                showErrorMessage("필수 입력 항목을 모두 입력해주세요.")
                return
            }

            // 약관 동의 확인
            const agreeTerms = document.getElementById("agreeTerms")
            if (!agreeTerms.checked) {
                showErrorMessage("이용약관과 개인정보처리방침에 동의해주세요.")
                return
            }

            // 주문 처리
            const formData = new FormData(checkoutForm)
            await processOrder(formData)
        })
    }

    // 쿠폰 적용 버튼 이벤트
    const applyCouponButton = document.getElementById("applyCouponBtn")
    if (applyCouponButton) {
        applyCouponButton.addEventListener("click", () => {
            const couponCode = document.getElementById("couponCode").value
            applyCoupon(couponCode)
        })
    }

    // 쿠폰 제거 버튼 이벤트
    const removeCouponButton = document.getElementById("removeCouponBtn")
    if (removeCouponButton) {
        removeCouponButton.addEventListener("click", () => {
            removeCoupon()
        })
    }
}

/**
 * 로딩 오버레이 표시/숨김
 */
function showLoading() {
    const loadingOverlay = document.getElementById("loadingOverlay")
    if (loadingOverlay) {
        loadingOverlay.classList.add("show")
    }
}

function hideLoading() {
    const loadingOverlay = document.getElementById("loadingOverlay")
    if (loadingOverlay) {
        loadingOverlay.classList.remove("show")
    }
}

/**
 * 숫자 포맷팅 (천 단위 콤마)
 */
function formatNumber(number) {
    return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
}

/**
 * 알림 메시지 표시 함수들
 */
function showSuccessMessage(message) {
    alert("성공: " + message)
    // 실제 구현 시에는 토스트 메시지 등으로 대체
}

function showErrorMessage(message) {
    alert("오류: " + message)
    // 실제 구현 시에는 토스트 메시지 등으로 대체
}

function showWarningMessage(message) {
    alert("경고: " + message)
    // 실제 구현 시에는 토스트 메시지 등으로 대체
}

