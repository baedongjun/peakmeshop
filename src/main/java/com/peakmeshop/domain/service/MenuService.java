package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.MenuDTO;
import com.peakmeshop.domain.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MenuService {
    
    // 메뉴 목록 조회
    List<MenuDTO> getMenus();

    List<MenuDTO> getMenusByType(String type);

    List<MenuDTO> updateMenuOrder(List<MenuDTO> menuDTOs);

    // 메뉴 상세 조회
    MenuDTO getMenuById(Long id);
    
    // 메뉴 생성
    MenuDTO createMenu(MenuDTO menuDTO);
    
    // 메뉴 수정
    MenuDTO updateMenu(Long id, MenuDTO menuDTO);
    
    // 메뉴 삭제
    void deleteMenu(Long id);
    
    // 메뉴 순서 변경
    void changeMenuOrder(Long id, int newOrder);
    
    // 메뉴 상태 변경
    void toggleMenuStatus(Long id);
    
    // 활성 메뉴 조회
    List<MenuDTO> getActiveMenus();
} 