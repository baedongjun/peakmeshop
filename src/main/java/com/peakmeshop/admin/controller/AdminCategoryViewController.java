package com.peakmeshop.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin")
public class AdminCategoryViewController {

    @GetMapping("/categories")
    public String categoriesList() {
        return "admin/categories/index";
    }

    @GetMapping("/categories/create")
    public String createCategoryForm() {
        return "admin/categories/form";
    }

    @GetMapping("/categories/{id}/edit")
    public String editCategoryForm(@PathVariable Long id) {
        return "admin/categories/form";
    }

    @GetMapping("/categories/{id}")
    public String categoryDetail(@PathVariable Long id) {
        return "admin/categories/detail";
    }
}

