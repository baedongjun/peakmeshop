$(document).ready(function () {
    $('#sidebarCollapse').on('click', function () {
        $('#sidebar').toggleClass('collapsed');
    });
});

// 사이드바 메뉴 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 현재 URL과 일치하는 메뉴 활성화
    const currentPath = window.location.pathname;
    const menuItems = document.querySelectorAll('#sidebar a');
    
    menuItems.forEach(item => {
        if (currentPath.startsWith(item.getAttribute('href'))) {
            item.classList.add('active');
            
            // 상위 메뉴가 있는 경우 펼치기
            const parentCollapse = item.closest('.collapse');
            if (parentCollapse) {
                parentCollapse.classList.add('show');
                const parentToggle = document.querySelector(`[data-target="#${parentCollapse.id}"]`);
                if (parentToggle) {
                    parentToggle.setAttribute('aria-expanded', 'true');
                }
            }
        }
    });

    // 토글 버튼 클릭 이벤트
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            document.getElementById('sidebar').classList.toggle('active');
        });
    }

    // 하위 메뉴 토글 이벤트
    const submenuToggles = document.querySelectorAll('#sidebar .has-submenu > a[data-toggle="collapse"]');
    submenuToggles.forEach(toggle => {
        toggle.addEventListener('click', function(e) {
            e.preventDefault();
            
            const isExpanded = this.getAttribute('aria-expanded') === 'true';
            this.setAttribute('aria-expanded', !isExpanded);
            
            const targetId = this.getAttribute('data-target').replace('#', '');
            const targetCollapse = document.getElementById(targetId);
            
            if (targetCollapse) {
                targetCollapse.classList.toggle('show');
            }
            
            // 다른 열린 메뉴 닫기
            submenuToggles.forEach(otherToggle => {
                if (otherToggle !== this) {
                    otherToggle.setAttribute('aria-expanded', 'false');
                    const otherId = otherToggle.getAttribute('data-target').replace('#', '');
                    const otherCollapse = document.getElementById(otherId);
                    if (otherCollapse) {
                        otherCollapse.classList.remove('show');
                    }
                }
            });
        });
    });
});