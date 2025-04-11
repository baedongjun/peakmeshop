package com.peakmeshop.admin.controller;

import com.peakmeshop.domain.service.AdminMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.peakmeshop.admin.controller")
@RequiredArgsConstructor
public class AdminLayoutController {

    private final AdminMenuService adminMenuService;

    @ModelAttribute("adminMenus")
    public Object getAdminMenus() {
        return adminMenuService.getActiveMenus();
    }
} 