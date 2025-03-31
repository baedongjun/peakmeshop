/**
 * PeakMeShop 공통 JavaScript 파일
 */

// DOM이 완전히 로드된 후 실행
document.addEventListener('DOMContentLoaded', function() {
    initTooltips();
    initDropdowns();
    setupQuantityControls();
    setupImageZoom();
    setupMobileMenu();
    setupScrollToTop();
});

/**
 * 툴팁 초기화
 */
function initTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * 드롭다운 초기화
 */
function initDropdowns() {
    const dropdownTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="dropdown"]'));
    dropdownTriggerList.map(function (dropdownTriggerEl) {
        return new bootstrap.Dropdown(dropdownTriggerEl);
    });
}

/**
 * 수량 조절 버튼 설정
 */
function setupQuantityControls() {
    // 수량 증가 버튼
    document.querySelectorAll('.quantity-increase').forEach(button => {
        button.addEventListener('click', function() {
            const input = this.parentNode.querySelector('input[type="number"]');
            const max = parseInt(input.getAttribute('max')) || 999;
            const currentValue = parseInt(input.value);

            if (currentValue < max) {
                input.value = currentValue + 1;
                // 변경 이벤트 발생
                input.dispatchEvent(new Event('change', { bubbles: true }));
            }
        });
    });

    // 수량 감소 버튼
    document.querySelectorAll('.quantity-decrease').forEach(button => {
        button.addEventListener('click', function() {
            const input = this.parentNode.querySelector('input[type="number"]');
            const min = parseInt(input.getAttribute('min')) || 1;
            const currentValue = parseInt(input.value);

            if (currentValue > min) {
                input.value = currentValue - 1;
                // 변경 이벤트 발생
                input.dispatchEvent(new Event('change', { bubbles: true }));
            }
        });
    });

    // 수량 입력 필드 변경 시 유효성 검사
    document.querySelectorAll('.quantity-input').forEach(input => {
        input.addEventListener('change', function() {
            const min = parseInt(this.getAttribute('min')) || 1;
            const max = parseInt(this.getAttribute('max')) || 999;
            let value = parseInt(this.value) || 0;

            if (value < min) {
                this.value = min;
            } else if (value > max) {
                this.value = max;
            }
        });
    });
}

/**
 * 이미지 확대 기능 설정
 */
function setupImageZoom() {
    const productImages = document.querySelectorAll('.product-image-zoom');

    productImages.forEach(image => {
        image.addEventListener('click', function() {
            const imageUrl = this.getAttribute('src');
            const modal = document.createElement('div');
            modal.className = 'image-zoom-modal';
            modal.innerHTML = `
                <div class="image-zoom-content">
                    <span class="image-zoom-close">&times;</span>
                    <img src="${imageUrl}" alt="확대 이미지">
                </div>
            `;

            document.body.appendChild(modal);
            document.body.style.overflow = 'hidden';

            // 모달 닫기 버튼
            modal.querySelector('.image-zoom-close').addEventListener('click', function() {
                document.body.removeChild(modal);
                document.body.style.overflow = '';
            });

            // 모달 외부 클릭 시 닫기
            modal.addEventListener('click', function(e) {
                if (e.target === modal) {
                    document.body.removeChild(modal);
                    document.body.style.overflow = '';
                }
            });
        });
    });
}

/**
 * 모바일 메뉴 설정
 */
function setupMobileMenu() {
    const mobileMenuToggle = document.querySelector('.navbar-toggler');
    const navbarCollapse = document.querySelector('.navbar-collapse');

    if (mobileMenuToggle && navbarCollapse) {
        mobileMenuToggle.addEventListener('click', function() {
            const expanded = this.getAttribute('aria-expanded') === 'true' || false;
            this.setAttribute('aria-expanded', !expanded);
            navbarCollapse.classList.toggle('show');

            if (!expanded) {
                document.body.style.overflow = 'hidden';
            } else {
                document.body.style.overflow = '';
            }
        });

        // 모바일 메뉴 외부 클릭 시 닫기
        document.addEventListener('click', function(e) {
            if (navbarCollapse.classList.contains('show') &&
                !navbarCollapse.contains(e.target) &&
                e.target !== mobileMenuToggle) {
                mobileMenuToggle.click();
            }
        });
    }
}

/**
 * 맨 위로 스크롤 버튼 설정
 */
function setupScrollToTop() {
    // 스크롤 버튼 생성
    const scrollButton = document.createElement('button');
    scrollButton.className = 'scroll-to-top';
    scrollButton.innerHTML = '<i class="fas fa-arrow-up"></i>';
    document.body.appendChild(scrollButton);

    // 스크롤 이벤트 리스너
    window.addEventListener('scroll', function() {
        if (window.pageYOffset > 300) {
            scrollButton.classList.add('show');
        } else {
            scrollButton.classList.remove('show');
        }
    });

    // 버튼 클릭 이벤트
    scrollButton.addEventListener('click', function() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
}

/**
 * 장바구니에 상품 추가
 * @param {number} productId - 상품 ID
 * @param {number} quantity - 수량
 * @param {Object} options - 옵션 (선택사항)
 */
function addToCart(productId, quantity = 1, options = {}) {
    // CSRF 토큰 가져오기
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // 장바구니 추가 요청
    fetch('/api/cart/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify({
            productId: productId,
            quantity: quantity,
            options: options
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showToast('장바구니에 상품이 추가되었습니다.', 'success');

                // 장바구니 아이콘 카운트 업데이트
                updateCartCount(data.cartItemCount);

                // 장바구니 미니 팝업 표시 (선택사항)
                showCartMiniPopup(data.product);
            } else {
                showToast(data.message || '장바구니 추가에 실패했습니다.', 'error');
            }
        })
        .catch(error => {
            console.error('장바구니 추가 오류:', error);
            showToast('장바구니 추가 중 오류가 발생했습니다.', 'error');
        });
}

/**
 * 장바구니 아이콘 카운트 업데이트
 * @param {number} count - 장바구니 아이템 수
 */
function updateCartCount(count) {
    const cartBadge = document.querySelector('.cart-badge');
    if (cartBadge) {
        cartBadge.textContent = count;
        cartBadge.style.display = count > 0 ? 'block' : 'none';
    }
}

/**
 * 장바구니 미니 팝업 표시
 * @param {Object} product - 상품 정보
 */
function showCartMiniPopup(product) {
    // 이미 존재하는 팝업 제거
    const existingPopup = document.querySelector('.cart-mini-popup');
    if (existingPopup) {
        document.body.removeChild(existingPopup);
    }

    // 새 팝업 생성
    const popup = document.createElement('div');
    popup.className = 'cart-mini-popup';
    popup.innerHTML = `
        <div class="cart-mini-popup-content">
            <div class="cart-mini-popup-header">
                <h5>장바구니에 상품이 추가되었습니다</h5>
                <button class="cart-mini-popup-close">&times;</button>
            </div>
            <div class="cart-mini-popup-body">
                <div class="cart-mini-product">
                    <img src="${product.imageUrl}" alt="${product.name}" class="cart-mini-product-image">
                    <div class="cart-mini-product-info">
                        <p class="cart-mini-product-name">${product.name}</p>
                        <p class="cart-mini-product-price">${formatCurrency(product.price)}</p>
                    </div>
                </div>
            </div>
            <div class="cart-mini-popup-footer">
                <a href="/shop" class="btn btn-outline-primary">쇼핑 계속하기</a>
                <a href="/cart" class="btn btn-primary">장바구니 보기</a>
            </div>
        </div>
    `;

    document.body.appendChild(popup);

    // 팝업 닫기 버튼
    popup.querySelector('.cart-mini-popup-close').addEventListener('click', function() {
        document.body.removeChild(popup);
    });

    // 5초 후 자동으로 닫기
    setTimeout(() => {
        if (document.body.contains(popup)) {
            document.body.removeChild(popup);
        }
    }, 5000);
}

/**
 * 토스트 메시지 표시
 * @param {string} message - 메시지 내용
 * @param {string} type - 메시지 타입 (success, error, info, warning)
 */
function showToast(message, type = 'info') {
    // 이미 존재하는 토스트 컨테이너 확인
    let toastContainer = document.querySelector('.toast-container');

    // 없으면 새로 생성
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.className = 'toast-container';
        document.body.appendChild(toastContainer);
    }

    // 토스트 요소 생성
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <div class="toast-content">
            <i class="${getToastIcon(type)}"></i>
            <div class="toast-message">${message}</div>
        </div>
        <button class="toast-close">&times;</button>
    `;

    toastContainer.appendChild(toast);

    // 토스트 닫기 버튼
    toast.querySelector('.toast-close').addEventListener('click', function() {
        toastContainer.removeChild(toast);
    });

    // 3초 후 자동으로 닫기
    setTimeout(() => {
        if (toastContainer.contains(toast)) {
            toastContainer.removeChild(toast);
        }
    }, 3000);
}

/**
 * 토스트 타입에 따른 아이콘 반환
 * @param {string} type - 메시지 타입
 * @returns {string} 아이콘 클래스
 */
function getToastIcon(type) {
    switch (type) {
        case 'success':
            return 'fas fa-check-circle';
        case 'error':
            return 'fas fa-exclamation-circle';
        case 'warning':
            return 'fas fa-exclamation-triangle';
        case 'info':
        default:
            return 'fas fa-info-circle';
    }
}

/**
 * 금액 형식화 (원화)
 * @param {number} amount - 금액
 * @returns {string} 형식화된 금액
 */
function formatCurrency(amount) {
    return new Intl.NumberFormat('ko-KR', {
        style: 'currency',
        currency: 'KRW'
    }).format(amount);
}

/**
 * 날짜 형식화
 * @param {string} dateString - 날짜 문자열
 * @param {string} format - 형식 (기본: 'YYYY-MM-DD')
 * @returns {string} 형식화된 날짜
 */
function formatDate(dateString, format = 'YYYY-MM-DD') {
    const date = new Date(dateString);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hours)
        .replace('mm', minutes)
        .replace('ss', seconds);
}

/**
 * 주소 검색 팝업 열기 (다음 우편번호 서비스)
 * @param {Function} callback - 주소 선택 후 콜백 함수
 */
function openAddressSearch(callback) {
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드
            let fullAddress = data.address;
            let extraAddress = '';

            // 법정동명이 있을 경우 추가
            if (data.bname !== '') {
                extraAddress += data.bname;
            }
            // 건물명이 있을 경우 추가
            if (data.buildingName !== '') {
                extraAddress += (extraAddress !== '' ? ', ' + data.buildingName : data.buildingName);
            }
            // 조합형 주소 추가
            if (extraAddress !== '') {
                fullAddress += ' (' + extraAddress + ')';
            }

            // 콜백 함수 호출
            callback({
                zipCode: data.zonecode,
                address: fullAddress,
                jibunAddress: data.jibunAddress,
                roadAddress: data.roadAddress
            });
        }
    }).open();
}