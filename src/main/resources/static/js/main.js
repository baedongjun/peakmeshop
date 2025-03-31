// 페이지 로드 완료 시 실행
document.addEventListener("DOMContentLoaded", () => {
    console.log("PeakMeShop 페이지가 로드되었습니다.")

    // 장바구니 아이템 수 업데이트
    updateCartItemCount()

    // 이미지 지연 로딩 초기화
    initLazyLoading()

    // 애니메이션 초기화
    initAnimations()
})

// 장바구니 아이템 수 업데이트 함수
function updateCartItemCount() {
    // 실제 구현에서는 서버에서 장바구니 정보를 가져와야 합니다
    const cartItemCount = localStorage.getItem("cartItemCount") || 0
    const cartBadge = document.getElementById("cart-badge")

    if (cartBadge) {
        cartBadge.textContent = cartItemCount
        cartBadge.style.display = cartItemCount > 0 ? "inline-block" : "none"
    }
}

// 이미지 지연 로딩 초기화 함수
function initLazyLoading() {
    const lazyImages = document.querySelectorAll("img[data-src]")

    if ("IntersectionObserver" in window) {
        const imageObserver = new IntersectionObserver((entries, observer) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    const image = entry.target
                    image.src = image.dataset.src
                    image.removeAttribute("data-src")
                    imageObserver.unobserve(image)
                }
            })
        })

        lazyImages.forEach((image) => {
            imageObserver.observe(image)
        })
    } else {
        // Intersection Observer를 지원하지 않는 브라우저를 위한 폴백
        lazyImages.forEach((image) => {
            image.src = image.dataset.src
            image.removeAttribute("data-src")
        })
    }
}

// 애니메이션 초기화 함수
function initAnimations() {
    if ("IntersectionObserver" in window) {
        const animatedElements = document.querySelectorAll(".animate-fade-in")

        const animationObserver = new IntersectionObserver(
            (entries, observer) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        entry.target.style.opacity = "1"
                        entry.target.style.transform = "translateY(0)"
                        observer.unobserve(entry.target)
                    }
                })
            },
            {
                threshold: 0.1,
            },
        )

        animatedElements.forEach((element) => {
            element.style.opacity = "0"
            element.style.transform = "translateY(20px)"
            element.style.transition = "opacity 0.8s ease, transform 0.8s ease"

            // 지연 시간 설정
            if (element.classList.contains("delay-1")) {
                element.style.transitionDelay = "0.2s"
            } else if (element.classList.contains("delay-2")) {
                element.style.transitionDelay = "0.4s"
            } else if (element.classList.contains("delay-3")) {
                element.style.transitionDelay = "0.6s"
            } else if (element.classList.contains("delay-4")) {
                element.style.transitionDelay = "0.8s"
            }

            animationObserver.observe(element)
        })
    }
}

// 상품 검색 함수
function searchProducts(query) {
    if (query.trim().length < 2) {
        alert("검색어는 2글자 이상 입력해주세요.")
        return false
    }
    return true
}

// 장바구니에 상품 추가 함수
function addToCart(productId, quantity = 1) {
    console.log(`상품 ID: ${productId}, 수량: ${quantity}를 장바구니에 추가합니다.`)

    // 실제 구현에서는 서버에 장바구니 추가 요청을 보내야 합니다
    // 예시 코드:
    /*
      fetch('/api/cart/items', {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json',
          },
          body: JSON.stringify({
              productId: productId,
              quantity: quantity
          })
      })
      .then(response => {
          if (response.ok) {
              return response.json();
          }
          throw new Error('장바구니 추가 실패');
      })
      .then(data => {
          // 성공 시 처리
          alert('상품이 장바구니에 추가되었습니다.');
          updateCartItemCount();
      })
      .catch(error => {
          // 오류 처리
          console.error('장바구니 추가 오류:', error);
          alert('장바구니에 추가하는 중 오류가 발생했습니다.');
      });
      */

    // 임시 구현 (로컬 스토리지 사용)
    const currentCount = Number.parseInt(localStorage.getItem("cartItemCount") || "0")
    localStorage.setItem("cartItemCount", currentCount + quantity)
    updateCartItemCount()

    // 알림 표시
    alert("상품이 장바구니에 추가되었습니다.")
}

// 위시리스트에 상품 추가 함수
function addToWishlist(productId) {
    console.log(`상품 ID: ${productId}를 위시리스트에 추가합니다.`)

    // 실제 구현에서는 서버에 위시리스트 추가 요청을 보내야 합니다
    // 임시 알림 표시
    alert("상품이 위시리스트에 추가되었습니다.")
}

// 스크롤 시 네비게이션 바 스타일 변경
window.addEventListener("scroll", () => {
    const navbar = document.querySelector(".navbar")
    if (window.scrollY > 50) {
        navbar.classList.add("navbar-scrolled")
    } else {
        navbar.classList.remove("navbar-scrolled")
    }
})

document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link');

    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath || (href !== '/' && currentPath.startsWith(href))) {
            link.classList.add('active');
        }
    });
});