package com.university.employee_management.controller;
import org.springframework.context.annotation.Profile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Profile("!render")
@Controller
@RequestMapping("/subadmin")
public class SubAdminController {

    // ✅ Sub-Admin Dashboard
    @GetMapping("/home")
    public String subAdminHome() {
        return "subadmin-home";   // MUST match templates/subadmin-home.html
    }
}
