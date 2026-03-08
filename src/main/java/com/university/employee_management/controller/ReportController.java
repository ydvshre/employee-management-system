package com.university.employee_management.controller;

import com.university.employee_management.model.Employee;
import com.university.employee_management.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private EmployeeRepository employeeRepository;

    // ---------------- OPEN REPORT PAGE ----------------
    @GetMapping
    public String reportPage() {
        return "report-search";
    }

    // ---------------- GENERATE REPORT ----------------
    @PostMapping("/search")
    public String generateReport(
        
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String employeeClass,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,
            Model model
    ) {
System.out.println("REPORT SEARCH HIT");
        // ✅ DECLARED ONCE — NO SCOPE ISSUE
        List<Employee> employees = new ArrayList<>();

        // 1️⃣ Single Employee (Highest Priority)
        if (employeeId != null) {
           Employee emp = employeeRepository.findById(employeeId).orElse(null);
if (emp != null) {
    employees.add(emp);
}
        }

        // 2️⃣ Date of Joining Range
        else if (fromDate != null) {
            if (toDate == null || toDate.isAfter(LocalDate.now())) {
                toDate = LocalDate.now();
            }
            employees = employeeRepository
                    .findByDateOfJoiningBetween(fromDate, toDate);
        }

        // 3️⃣ Department
        else if (department != null && !department.trim().isEmpty()) {
            employees = employeeRepository
                    .findByDepartmentIgnoreCase(department.trim());
        }

        // 4️⃣ Employee Class
        else if (employeeClass != null && !employeeClass.trim().isEmpty()) {
            employees = employeeRepository
                    .findByEmployeeClassIgnoreCase(employeeClass.trim());
        }

        // 5️⃣ Designation
        else if (designation != null && !designation.trim().isEmpty()) {
            employees = employeeRepository
                    .findByDesignationIgnoreCase(designation.trim());
        }

        // ---------------- SEND DATA TO VIEW ----------------
        model.addAttribute("employees", employees);

        if (employees.isEmpty()) {
            model.addAttribute("message", "No records found for given criteria");
        }

        return "report-result";
    }
}