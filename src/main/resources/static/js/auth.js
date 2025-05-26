// auth.js
document.addEventListener("DOMContentLoaded", () => {
    initSignupForm();
});

function initSignupForm() {
    const signupForm = document.getElementById('signupForm');

    if (signupForm) {
        signupForm.addEventListener('submit', function(event) {
            event.preventDefault();

            // 폼 데이터 수집
            const formData = {
                userId: document.getElementById('userId').value,
                email: document.getElementById('email').value,
                password: document.getElementById('password').value,
                passwordConfirm: document.getElementById('passwordConfirm').value,
                name: document.getElementById('name').value,
                phone: document.getElementById('phone') ? document.getElementById('phone').value : '',
                agreeTerms: document.getElementById('agreeTerms') ? document.getElementById('agreeTerms').checked : false,
                agreeMarketing: document.getElementById('agreeMarketing') ? document.getElementById('agreeMarketing').checked : false
            };

            // 폼 필드 초기화 (오류 표시 제거)
            clearFormErrors();

            // AJAX 요청 전송
            fetch('/api/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': getCSRFToken()
                },
                body: JSON.stringify(formData)
            })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            try {
                                // JSON 파싱 시도
                                const errors = JSON.parse(text);
                                throw errors;
                            } catch (e) {
                                // JSON 파싱 실패 시 일반 텍스트로 처리
                                throw { message: text || '회원가입 처리 중 오류가 발생했습니다.' };
                            }
                        });
                    }
                    return response.text().then(text => {
                        try {
                            return text ? JSON.parse(text) : {};
                        } catch (e) {
                            return {};
                        }
                    });
                })
                .then(data => {
                    // 성공 처리
                    console.log('회원가입 성공:', data);

                    // 성공 메시지 표시
                    showSuccessMessage('회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.');

                    // 로그인 페이지로 리다이렉트
                    setTimeout(() => {
                        window.location.href = '/login?registered=true';
                    }, 1500);
                })
                .catch(errors => {
                    // 오류 처리
                    console.error('회원가입 오류:', errors);

                    if (typeof errors === 'object' && errors !== null) {
                        if (errors.message) {
                            // 일반 오류 메시지
                            showGeneralError(errors.message);
                        } else {
                            // 필드별 오류 메시지
                            Object.keys(errors).forEach(field => {
                                showFieldError(field, errors[field]);
                            });
                        }
                    } else {
                        // 일반 오류 메시지 표시
                        showGeneralError('회원가입 처리 중 오류가 발생했습니다.');
                    }
                });
        });
    }
}

// CSRF 토큰 가져오기
function getCSRFToken() {
    const tokenElement = document.querySelector('meta[name="_csrf"]');
    return tokenElement ? tokenElement.getAttribute('content') : '';
}

// 폼 오류 초기화
function clearFormErrors() {
    // 모든 오류 메시지 및 오류 클래스 제거
    document.querySelectorAll('.is-invalid').forEach(element => {
        element.classList.remove('is-invalid');
    });

    document.querySelectorAll('.invalid-feedback').forEach(element => {
        element.style.display = 'none';
    });

    // 일반 오류 메시지 제거
    const errorAlert = document.querySelector('.alert-danger');
    if (errorAlert) {
        errorAlert.style.display = 'none';
    }
}

// 필드별 오류 메시지 표시
function showFieldError(fieldName, errorMessage) {
    const field = document.getElementById(fieldName);
    if (field) {
        field.classList.add('is-invalid');

        let feedbackElement = field.nextElementSibling;
        while (feedbackElement && !feedbackElement.classList.contains('invalid-feedback')) {
            feedbackElement = feedbackElement.nextElementSibling;
        }

        if (feedbackElement) {
            feedbackElement.textContent = errorMessage;
            feedbackElement.style.display = 'block';
        } else {
            // 피드백 요소가 없으면 생성
            const newFeedback = document.createElement('div');
            newFeedback.className = 'invalid-feedback';
            newFeedback.textContent = errorMessage;
            newFeedback.style.display = 'block';
            field.parentNode.appendChild(newFeedback);
        }
    }
}

// 일반 오류 메시지 표시
function showGeneralError(message) {
    let errorAlert = document.querySelector('.alert-danger');

    if (!errorAlert) {
        // 오류 알림 요소 생성
        errorAlert = document.createElement('div');
        errorAlert.className = 'alert alert-danger';

        // 폼 앞에 삽입
        const form = document.querySelector('form');
        if (form) {
            form.parentNode.insertBefore(errorAlert, form);
        }
    }

    errorAlert.textContent = message;
    errorAlert.style.display = 'block';
}

// 성공 메시지 표시
function showSuccessMessage(message) {
    let successAlert = document.querySelector('.alert-success');

    if (!successAlert) {
        // 성공 알림 요소 생성
        successAlert = document.createElement('div');
        successAlert.className = 'alert alert-success';

        // 폼 앞에 삽입
        const form = document.querySelector('form');
        if (form) {
            form.parentNode.insertBefore(successAlert, form);
        }
    }

    successAlert.textContent = message;
    successAlert.style.display = 'block';
}