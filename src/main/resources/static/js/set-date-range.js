/**
 * 날짜 범위 설정 함수
 * @param {string} range - 설정할 날짜 범위 ('today', 'yesterday', 'week', 'month', '3months', '6months', 'year')
 * @param {string} startDateId - 시작 날짜 입력 요소의 ID (기본값: 'startDate')
 * @param {string} endDateId - 종료 날짜 입력 요소의 ID (기본값: 'endDate')
 */
function setDateRange(range, startDateId = "startDate", endDateId = "endDate") {
    const today = new Date()
    const startDate = new Date()

    switch (range) {
        case "today":
            // 시작일과 종료일 모두 오늘
            break
        case "yesterday":
            // 어제
            startDate.setDate(today.getDate() - 1)
            today.setDate(today.getDate() - 1)
            break
        case "week":
            // 1주일 전부터 오늘까지
            startDate.setDate(today.getDate() - 7)
            break
        case "month":
            // 1개월 전부터 오늘까지
            startDate.setMonth(today.getMonth() - 1)
            break
        case "3months":
            // 3개월 전부터 오늘까지
            startDate.setMonth(today.getMonth() - 3)
            break
        case "6months":
            // 6개월 전부터 오늘까지
            startDate.setMonth(today.getMonth() - 6)
            break
        case "year":
            // 1년 전부터 오늘까지
            startDate.setFullYear(today.getFullYear() - 1)
            break
        default:
            console.error("유효하지 않은 날짜 범위입니다.")
            return
    }

    // 날짜를 YYYY-MM-DD 형식으로 변환
    document.getElementById(startDateId).value = formatDateForInput(startDate)
    document.getElementById(endDateId).value = formatDateForInput(today)
}

/**
 * Date 객체를 HTML input[type="date"]에 사용할 수 있는 형식(YYYY-MM-DD)으로 변환
 * @param {Date} date - 변환할 Date 객체
 * @returns {string} YYYY-MM-DD 형식의 문자열
 */
function formatDateForInput(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, "0")
    const day = String(date.getDate()).padStart(2, "0")
    return `${year}-${month}-${day}`
}
