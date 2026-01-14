package com.university.employee_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

// @Controller
public class AdminEmployeeController {

    @GetMapping("/admin/addEmployee")
    public String addEmployeePage() {
        return "add-employee";
    }

    @PostMapping("/admin/saveEmployee")
    public String saveEmployee() {
        // For now, just redirect back to dashboard
        return "redirect:/admin/home";
    }
}
