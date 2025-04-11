package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.AdminMenuDTO;
import java.util.List;

public interface AdminMenuService {
    /**
     * 모든 관리자 메뉴를 조회합니다.
     * @return 관리자 메뉴 목록
     */
    List<AdminMenuDTO> getAllMenus();

    /**
     * 상위 메뉴만 조회합니다.
     * @return 상위 메뉴 목록
     */
    List<AdminMenuDTO> getRootMenus();

    /**
     * 특정 상위 메뉴의 하위 메뉴를 조회합니다.
     * @param parentId 상위 메뉴 ID
     * @return 하위 메뉴 목록
     */
    List<AdminMenuDTO> getChildMenus(Long parentId);

    /**
     * 메뉴를 생성합니다.
     * @param menuDTO 메뉴 정보
     * @return 생성된 메뉴
     */
    AdminMenuDTO createMenu(AdminMenuDTO menuDTO);

    /**
     * 메뉴를 수정합니다.
     * @param id 메뉴 ID
     * @param menuDTO 수정할 메뉴 정보
     * @return 수정된 메뉴
     */
    AdminMenuDTO updateMenu(Long id, AdminMenuDTO menuDTO);

    /**
     * 메뉴를 삭제합니다.
     * @param id 메뉴 ID
     */
    void deleteMenu(Long id);

    /**
     * 메뉴 순서를 업데이트합니다.
     * @param menuDTOs 순서가 변경된 메뉴 목록
     * @return 업데이트된 메뉴 목록
     */
    List<AdminMenuDTO> updateMenuOrder(List<AdminMenuDTO> menuDTOs);

    /**
     * 활성화된 메뉴만 조회합니다.
     * @return 활성화된 메뉴 목록
     */
    List<AdminMenuDTO> getActiveMenus();
} 