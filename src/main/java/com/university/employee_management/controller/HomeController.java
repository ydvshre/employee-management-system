package com.university.employee_management.controller;
import org.springframework.context.annotation.Profile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Profile("render")
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}

