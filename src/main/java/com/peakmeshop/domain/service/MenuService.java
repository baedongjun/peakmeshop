package com.peakmeshop.domain.service;

import com.peakmeshop.api.dto.MenuDTO;
import java.util.List;

public interface MenuService {
    List<MenuDTO> getMenusByType(String type);
    MenuDTO createMenu(MenuDTO menuDTO);
    MenuDTO updateMenu(Long id, MenuDTO menuDTO);
    void deleteMenu(Long id);
    List<MenuDTO> updateMenuOrder(List<MenuDTO> menuDTOs);
} 