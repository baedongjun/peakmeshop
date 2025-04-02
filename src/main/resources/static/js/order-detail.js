document.addEventListener("DOMContentLoaded", () => {
    // Get order ID from URL parameter
    const urlParams = new URLSearchParams(window.location.search)
    const orderId = urlParams.get("id")

    if (!orderId) {
        showError("주문 ID가 제공되지 않았습니다.")
        return
    }

    // Fetch order details
    fetchOrderDetails(orderId)

    // Add event listeners
    document.getElementById("cancelOrderBtn")?.addEventListener("click", () => {
        cancelOrder(orderId)
    })
})

function fetchOrderDetails(orderId) {
    fetch(`/api/orders/${orderId}`)
        .then((response) => {
            if (!response.ok) {
                throw new Error("주문 정보를 가져오는데 실패했습니다.")
            }
            return response.json()
        })
        .then((order) => {
            displayOrderDetails(order)
        })
        .catch((error) => {
            showError(error.message)
        })
}

function displayOrderDetails(order) {
    // Update order header information
    document.getElementById("orderNumber").textContent = order.orderNumber
    document.getElementById("orderDate").textContent = formatDate(order.createdAt)
    document.getElementById("orderStatus").textContent = getStatusText(order.status)
    document.getElementById("orderStatus").className = `badge ${getStatusClass(order.status)}`

    // Update customer information
    const customerInfo = document.getElementById("customerInfo")
    customerInfo.innerHTML = `
        <p><strong>이름:</strong> ${order.shippingAddress.name}</p>
        <p><strong>이메일:</strong> ${order.email}</p>
        <p><strong>전화번호:</strong> ${order.shippingAddress.phone}</p>
    `

    // Update shipping address
    const shippingAddress = document.getElementById("shippingAddress")
    shippingAddress.innerHTML = `
        <p>${order.shippingAddress.name}</p>
        <p>${order.shippingAddress.address1}</p>
        <p>${order.shippingAddress.address2 || ""}</p>
        <p>${order.shippingAddress.city}, ${order.shippingAddress.state} ${order.shippingAddress.zipCode}</p>
        <p>${order.shippingAddress.country}</p>
        <p>${order.shippingAddress.phone}</p>
    `

    // Update billing address
    const billingAddress = document.getElementById("billingAddress")
    billingAddress.innerHTML = `
        <p>${order.billingAddress.name}</p>
        <p>${order.billingAddress.address1}</p>
        <p>${order.billingAddress.address2 || ""}</p>
        <p>${order.billingAddress.city}, ${order.billingAddress.state} ${order.billingAddress.zipCode}</p>
        <p>${order.billingAddress.country}</p>
        <p>${order.billingAddress.phone}</p>
    `

    // Update payment information
    const paymentInfo = document.getElementById("paymentInfo")
    paymentInfo.innerHTML = `
        <p><strong>결제 방법:</strong> ${order.paymentMethod}</p>
        <p><strong>결제 상태:</strong> <span class="badge ${getPaymentStatusClass(order.paymentStatus)}">${getPaymentStatusText(order.paymentStatus)}</span></p>
    `

    // Update order items
    const orderItemsContainer = document.getElementById("orderItems")
    orderItemsContainer.innerHTML = ""

    order.items.forEach((item) => {
        const itemRow = document.createElement("tr")
        itemRow.innerHTML = `
            <td>
                <div class="d-flex align-items-center">
                    <img src="${item.product.thumbnailUrl}" alt="${item.product.name}" class="img-fluid rounded" style="width: 60px;">
                    <div class="ms-3">
                        <h6 class="mb-0">${item.product.name}</h6>
                        ${item.options ? `<small class="text-muted">${formatOptions(item.options)}</small>` : ""}
                    </div>
                </div>
            </td>
            <td>${formatCurrency(item.price)}</td>
            <td>${item.quantity}</td>
            <td>${formatCurrency(item.price * item.quantity)}</td>
        `
        orderItemsContainer.appendChild(itemRow)
    })

    // Update order summary
    document.getElementById("subtotal").textContent = formatCurrency(order.subtotal)
    document.getElementById("shipping").textContent = formatCurrency(order.shippingFee)
    document.getElementById("tax").textContent = formatCurrency(order.tax)

    if (order.discount > 0) {
        document.getElementById("discountRow").style.display = ""
        document.getElementById("discount").textContent = `-${formatCurrency(order.discount)}`
    } else {
        document.getElementById("discountRow").style.display = "none"
    }

    document.getElementById("total").textContent = formatCurrency(order.total)

    // Show tracking information if available
    if (order.trackingNumber) {
        document.getElementById("trackingInfo").style.display = ""
        document.getElementById("trackingNumber").textContent = order.trackingNumber
        document.getElementById("trackingUrl").href = order.trackingUrl || "#"
    } else {
        document.getElementById("trackingInfo").style.display = "none"
    }

    // Show or hide cancel button based on order status
    const cancelOrderBtn = document.getElementById("cancelOrderBtn")
    if (cancelOrderBtn) {
        if (["PENDING", "PROCESSING"].includes(order.status)) {
            cancelOrderBtn.style.display = ""
        } else {
            cancelOrderBtn.style.display = "none"
        }
    }
}

function cancelOrder(orderId) {
    if (!confirm("정말로 이 주문을 취소하시겠습니까?")) {
        return
    }

    fetch(`/api/orders/${orderId}/cancel`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("주문 취소에 실패했습니다.")
            }
            return response.json()
        })
        .then((data) => {
            alert("주문이 성공적으로 취소되었습니다.")
            location.reload()
        })
        .catch((error) => {
            showError(error.message)
        })
}

function formatOptions(options) {
    if (!options || options.length === 0) return ""

    return options.map((option) => `${option.name}: ${option.value}`).join(", ")
}

function formatDate(dateString) {
    const date = new Date(dateString)
    return date.toLocaleDateString("ko-KR", {
        year: "numeric",
        month: "long",
        day: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    })
}

function formatCurrency(amount) {
    return new Intl.NumberFormat("ko-KR", {
        style: "currency",
        currency: "KRW",
    }).format(amount)
}

function getStatusText(status) {
    const statusMap = {
        PENDING: "대기 중",
        PROCESSING: "처리 중",
        SHIPPED: "배송 중",
        DELIVERED: "배송 완료",
        CANCELLED: "취소됨",
        REFUNDED: "환불됨",
    }

    return statusMap[status] || status
}

function getStatusClass(status) {
    const classMap = {
        PENDING: "bg-warning",
        PROCESSING: "bg-info",
        SHIPPED: "bg-primary",
        DELIVERED: "bg-success",
        CANCELLED: "bg-danger",
        REFUNDED: "bg-secondary",
    }

    return classMap[status] || "bg-secondary"
}

function getPaymentStatusText(status) {
    const statusMap = {
        PENDING: "대기 중",
        PAID: "결제 완료",
        FAILED: "결제 실패",
        REFUNDED: "환불됨",
    }

    return statusMap[status] || status
}

function getPaymentStatusClass(status) {
    const classMap = {
        PENDING: "bg-warning",
        PAID: "bg-success",
        FAILED: "bg-danger",
        REFUNDED: "bg-secondary",
    }

    return classMap[status] || "bg-secondary"
}

function showError(message) {
    const alertContainer = document.getElementById("alertContainer")
    alertContainer.innerHTML = `
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `
}

