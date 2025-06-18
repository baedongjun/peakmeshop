-- 기존 메뉴 데이터 삭제
DELETE FROM admin_menus;

-- 최상위 메뉴 추가
INSERT INTO admin_menus (name, url, icon, sort_order, is_visible, parent_id, created_at, updated_at) VALUES
('대시보드', '/admin', 'fas fa-tachometer-alt', 1, true, null, NOW(), NOW()),
('상품 관리', '/admin/products', 'fas fa-box', 2, true, null, NOW(), NOW()),
('주문 관리', '/admin/orders', 'fas fa-shopping-cart', 3, true, null, NOW(), NOW()),
('회원 관리', '/admin/members', 'fas fa-users', 4, true, null, NOW(), NOW()),
('게시판 관리', '/admin/boards', 'fas fa-clipboard', 5, true, null, NOW(), NOW()),
('마케팅 관리', '/admin/marketing', 'fas fa-bullhorn', 6, true, null, NOW(), NOW()),
('통계', '/admin/statistics', 'fas fa-chart-bar', 7, true, null, NOW(), NOW()),
('설정', '/admin/settings', 'fas fa-cog', 8, true, null, NOW(), NOW());

-- 상품 관리 하위 메뉴
INSERT INTO admin_menus (name, url, icon, sort_order, is_visible, parent_id, created_at, updated_at) VALUES
('상품 목록', '/admin/products', 'fas fa-list', 1, true, 
 (SELECT id FROM admin_menus WHERE url = '/admin/products'), NOW(), NOW()),
('상품 등록', '/admin/products/new', 'fas fa-plus', 2, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/products'), NOW(), NOW()),
('카테고리 관리', '/admin/categories', 'fas fa-tags', 3, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/products'), NOW(), NOW()),
('브랜드 관리', '/admin/brands', 'fas fa-copyright', 4, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/products'), NOW(), NOW()),
('공급사 관리', '/admin/suppliers', 'fas fa-truck', 5, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/products'), NOW(), NOW()),
('상품 리뷰 관리', '/admin/products/reviews', 'fas fa-star', 6, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/products'), NOW(), NOW()),
('상품 문의 관리', '/admin/products/qnas', 'fas fa-question-circle', 7, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/products'), NOW(), NOW()),
('재고 관리', '/admin/products/inventory', 'fas fa-warehouse', 8, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/products'), NOW(), NOW());

-- 주문 관리 하위 메뉴
INSERT INTO admin_menus (name, url, icon, sort_order, is_visible, parent_id, created_at, updated_at) VALUES
('주문 목록', '/admin/orders', 'fas fa-list', 1, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/orders'), NOW(), NOW()),
('배송 관리', '/admin/shipments', 'fas fa-truck', 2, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/orders'), NOW(), NOW()),
('배송비 설정', '/admin/shipments/fees', 'fas fa-dollar-sign', 3, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/orders'), NOW(), NOW()),
('배송 지역 설정', '/admin/shipments/areas', 'fas fa-map-marker-alt', 4, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/orders'), NOW(), NOW());

-- 회원 관리 하위 메뉴
INSERT INTO admin_menus (name, url, icon, sort_order, is_visible, parent_id, created_at, updated_at) VALUES
('회원 목록', '/admin/members', 'fas fa-list', 1, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/members'), NOW(), NOW()),
('회원 등급 관리', '/admin/member-grades', 'fas fa-star', 2, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/members'), NOW(), NOW()),
('포인트 관리', '/admin/points', 'fas fa-coins', 3, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/members'), NOW(), NOW());

-- 게시판 관리 하위 메뉴
INSERT INTO admin_menus (name, url, icon, sort_order, is_visible, parent_id, created_at, updated_at) VALUES
('공지사항', '/admin/notices', 'fas fa-bullhorn', 1, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/boards'), NOW(), NOW()),
('FAQ', '/admin/faqs', 'fas fa-question-circle', 2, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/boards'), NOW(), NOW()),
('1:1 문의', '/admin/inquiries', 'fas fa-comments', 3, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/boards'), NOW(), NOW()),
('이메일 발송 관리', '/admin/emails', 'fas fa-envelope', 4, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/boards'), NOW(), NOW()),
('이메일 템플릿 관리', '/admin/email-templates', 'fas fa-file-alt', 5, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/boards'), NOW(), NOW());

-- 마케팅 관리 하위 메뉴
INSERT INTO admin_menus (name, url, icon, sort_order, is_visible, parent_id, created_at, updated_at) VALUES
('쿠폰 관리', '/admin/coupons', 'fas fa-ticket-alt', 1, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/marketing'), NOW(), NOW()),
('프로모션 관리', '/admin/promotions', 'fas fa-percent', 2, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/marketing'), NOW(), NOW());

-- 통계 하위 메뉴
INSERT INTO admin_menus (name, url, icon, sort_order, is_visible, parent_id, created_at, updated_at) VALUES
('통계 대시보드', '/admin/statistics/dashboard', 'fas fa-tachometer-alt', 1, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/statistics'), NOW(), NOW()),
('메인 통계', '/admin/statistics', 'fas fa-chart-line', 2, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/statistics'), NOW(), NOW()),
('매출 통계', '/admin/statistics/sales', 'fas fa-chart-line', 3, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/statistics'), NOW(), NOW()),
('상품 통계', '/admin/statistics/products', 'fas fa-chart-line', 3, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/statistics'), NOW(), NOW()),
('회원 통계', '/admin/statistics/members', 'fas fa-chart-line', 4, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/statistics'), NOW(), NOW()),
('방문자 통계', '/admin/statistics/visitors', 'fas fa-chart-pie', 5, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/statistics'), NOW(), NOW());

-- 설정 하위 메뉴
INSERT INTO admin_menus (name, url, icon, sort_order, is_visible, parent_id, created_at, updated_at) VALUES
('기본 설정', '/admin/settings', 'fas fa-sliders-h', 1, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/settings'), NOW(), NOW()),
('메뉴 관리', '/admin/menus', 'fas fa-bars', 2, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/settings'), NOW(), NOW()),
('배너 관리', '/admin/banners', 'fas fa-images', 3, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/settings'), NOW(), NOW()),
('팝업 관리', '/admin/popups', 'fas fa-window-restore', 4, true,
 (SELECT id FROM admin_menus WHERE url = '/admin/settings'), NOW(), NOW()); 