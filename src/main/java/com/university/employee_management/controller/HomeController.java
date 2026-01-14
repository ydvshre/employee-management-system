package com.university.employee_management.controller;
import org.springframework.context.annotation.Profile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Profile("render")
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
    @GetMapping("/login")
public String login(@RequestParam String role) {
    if ("admin".equals(role)) {
        return "redirect:/admin/home";
    } else if ("subadmin".equals(role)) {
        return "redirect:/subadmin/home";
    }
    return "index";
}

}

