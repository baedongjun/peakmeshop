package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminBrandViewController {

    @GetMapping("/brands")
    public String brandsList() {
        return "admin/brands/index";
    }

    @GetMapping("/brands/create")
    public String createBrandForm() {
        return "admin/brands/form";
    }

    @GetMapping("/brands/{id}/edit")
    public String editBrandForm(@PathVariable Long id) {
        return "admin/brands/form";
    }

    @GetMapping("/brands/{id}")
    public String brandDetail(@PathVariable Long id) {
        return "admin/brands/detail";
    }
}

