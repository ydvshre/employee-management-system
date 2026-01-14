package com.university.employee_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logout")
public class LogoutController {

    @PostMapping("/admin")
    public String adminLogout() {
        return "redirect:/login?role=admin";
    }

    @PostMapping("/subadmin")
    public String subAdminLogout() {
        return "redirect:/login?role=subadmin";
    }
}
