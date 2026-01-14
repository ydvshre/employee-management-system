package com.university.employee_management.controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(@RequestParam String role, Model model) {
        model.addAttribute("role", role);
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            Model model
    ) {

        if ("admin".equals(role)) {
            if ("admin".equals(username) && "admin123".equals(password)) {
                return "redirect:/admin/home";
            }
        }

        if ("subadmin".equals(role)) {
            if ("subadmin".equals(username) && "subadmin123".equals(password)) {
                return "redirect:/subadmin/home";
            }
        }

        model.addAttribute("role", role);
        model.addAttribute("error", true);
        return "login";
    }
}
