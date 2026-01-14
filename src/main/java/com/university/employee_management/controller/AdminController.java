package com.university.employee_management.controller;

import com.university.employee_management.model.Employee;
import com.university.employee_management.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private EmployeeRepository employeeRepository;

    // ================= DASHBOARD =================
    @GetMapping("/admin/home")
    public String adminHome() {
        return "admin-home";
    }

    // ================= ADD EMPLOYEE =================
    @GetMapping("/admin/addEmployee")
    public String addEmployeePage(Model model) {

        Integer lastId = employeeRepository
                .findTopByOrderByEmployeeIdDesc()
                .map(Employee::getEmployeeId)
                .orElse(0);

        model.addAttribute("nextEmployeeId", lastId + 1);
        model.addAttribute("employee", new Employee());

        return "add-employee";
    }

 @PostMapping("/admin/saveEmployee")
public String saveEmployee(
        @Valid @ModelAttribute("employee") Employee employee,
        BindingResult result,
        Model model
) {

    // 1️⃣ Bean Validation (patterns, @AssertTrue, age, etc.)
    if (result.hasErrors()) {
        return "add-employee";
    }

    // 2️⃣ UNIQUE AADHAAR CHECK (only if provided)
    if (employee.getAadhaarNumber() != null &&
        !employee.getAadhaarNumber().isBlank()) {

        if (employeeRepository
                .findByAadhaarNumber(employee.getAadhaarNumber())
                .isPresent()) {

            result.rejectValue(
                    "aadhaarNumber",
                    "error.employee",
                    "Aadhaar number already exists"
            );
        }
    }

    // 3️⃣ UNIQUE LICENSE CHECK (only if provided)
    if (employee.getDrivingLicenseNumber() != null &&
        !employee.getDrivingLicenseNumber().isBlank()) {

        if (employeeRepository
                .findByDrivingLicenseNumber(employee.getDrivingLicenseNumber())
                .isPresent()) {

            result.rejectValue(
                    "drivingLicenseNumber",
                    "error.employee",
                    "Driving License number already exists"
            );
        }
    }

    // 4️⃣ If ANY uniqueness error → return form
    if (result.hasErrors()) {
        return "add-employee";
    }

    // 5️⃣ SAVE (DB-level unique: email/mobile safety)
    try {
        employeeRepository.save(employee);
        return "redirect:/admin/addEmployee?success";
    }
    catch (org.springframework.dao.DataIntegrityViolationException ex) {

        String msg = ex.getMostSpecificCause().getMessage();

        if (msg.contains("mobile")) {
            model.addAttribute("error", "Mobile number already exists");
        } else if (msg.contains("email")) {
            model.addAttribute("error", "Email already exists");
        } else if (msg.contains("aadhaar")) {
            model.addAttribute("error", "Aadhaar number already exists");
        } else if (msg.contains("driving")) {
            model.addAttribute("error", "Driving License number already exists");
        } else {
            model.addAttribute("error", "Duplicate data detected");
        }

        return "add-employee";
    }
}



    // ================= VIEW EMPLOYEES =================
    @GetMapping("/employee/view")
    public String viewEmployees(
            @RequestParam(value = "role", defaultValue = "admin") String role,
            Model model
    ) {
        model.addAttribute("employees", employeeRepository.findAll());
        model.addAttribute("backUrl",
                "subadmin".equals(role) ? "/subadmin/home" : "/admin/home");
        return "view-employee";
    }

    // ================= SEARCH =================
    @GetMapping("/employee/search")
    public String searchEmployee(
            @RequestParam Integer employeeId,
            Model model
    ) {
        employeeRepository.findById(employeeId)
                .ifPresent(emp -> model.addAttribute("employees", List.of(emp)));

        return "view-employee";
    }

    // ================= EDIT =================
    @GetMapping("/admin/edit")
    public String editEmployeeSearchPage() {
        return "edit-employee-search";
    }

   @GetMapping("/admin/editEmployee")
public String showEditForm(@RequestParam Integer employeeId, Model model) {

    return employeeRepository.findById(employeeId)
            .map(emp -> {
                model.addAttribute("employee", emp);
                model.addAttribute("dobMax", LocalDate.now().minusYears(18));
                return "edit-employee-form";
            })
            .orElse("edit-employee-search");
}


   @PostMapping("/admin/updateEmployee")
public String updateEmployee(
        @Valid @ModelAttribute("employee") Employee formEmployee,
        BindingResult result,
        Model model
) {

    // ---------- 1️⃣ BEAN VALIDATION ----------
    if (result.hasErrors()) {
        model.addAttribute("dobMax", LocalDate.now().minusYears(18));
        return "edit-employee-form";
    }

    // ---------- 2️⃣ UNIQUE AADHAAR CHECK ----------
    if (formEmployee.getAadhaarNumber() != null &&
        !formEmployee.getAadhaarNumber().isBlank()) {

        employeeRepository
            .findByAadhaarNumber(formEmployee.getAadhaarNumber())
            .ifPresent(existing -> {
                if (!existing.getEmployeeId()
                        .equals(formEmployee.getEmployeeId())) {

                    result.rejectValue(
                        "aadhaarNumber",
                        "error.employee",
                        "Aadhaar number already exists"
                    );
                }
            });
    }

    // ---------- 3️⃣ UNIQUE LICENSE CHECK ----------
    if (formEmployee.getDrivingLicenseNumber() != null &&
        !formEmployee.getDrivingLicenseNumber().isBlank()) {

        employeeRepository
            .findByDrivingLicenseNumber(formEmployee.getDrivingLicenseNumber())
            .ifPresent(existing -> {
                if (!existing.getEmployeeId()
                        .equals(formEmployee.getEmployeeId())) {

                    result.rejectValue(
                        "drivingLicenseNumber",
                        "error.employee",
                        "Driving License number already exists"
                    );
                }
            });
    }

    // ---------- 4️⃣ RETURN IF ERRORS ----------
    if (result.hasErrors()) {
        model.addAttribute("dobMax", LocalDate.now().minusYears(18));
        return "edit-employee-form";
    }

    // ---------- 5️⃣ LOAD EXISTING EMPLOYEE ----------
    Employee dbEmployee = employeeRepository
            .findById(formEmployee.getEmployeeId())
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    // ---------- 6️⃣ DEPARTMENT ----------
    dbEmployee.setTitle(formEmployee.getTitle());
    dbEmployee.setFirstName(formEmployee.getFirstName());
    dbEmployee.setMiddleName(formEmployee.getMiddleName());
    dbEmployee.setLastName(formEmployee.getLastName());
    dbEmployee.setDepartment(formEmployee.getDepartment());
    dbEmployee.setDesignation(formEmployee.getDesignation());
    dbEmployee.setPayScale(formEmployee.getPayScale());
    dbEmployee.setBasicSalary(formEmployee.getBasicSalary());
    dbEmployee.setEmployeeClass(formEmployee.getEmployeeClass());
    dbEmployee.setNatureOfAppointment(formEmployee.getNatureOfAppointment());
    dbEmployee.setDateOfJoining(formEmployee.getDateOfJoining());
    dbEmployee.setConfirmationDate(formEmployee.getConfirmationDate());

    // ---------- 7️⃣ CONTRACT LOGIC ----------
    if ("Contract".equalsIgnoreCase(formEmployee.getNatureOfAppointment())) {
        dbEmployee.setContractStartDate(formEmployee.getContractStartDate());
        dbEmployee.setContractEndDate(formEmployee.getContractEndDate());
    } else {
        dbEmployee.setContractStartDate(null);
        dbEmployee.setContractEndDate(null);
    }

    // ---------- 8️⃣ PERSONAL ----------
    dbEmployee.setFatherName(formEmployee.getFatherName());
    dbEmployee.setMotherName(formEmployee.getMotherName());
    dbEmployee.setMaritalStatus(formEmployee.getMaritalStatus());
    dbEmployee.setSpouseName(formEmployee.getSpouseName());
    dbEmployee.setDateOfBirth(formEmployee.getDateOfBirth());
    dbEmployee.setGender(formEmployee.getGender());
    dbEmployee.setCategory(formEmployee.getCategory());

    // ---------- 9️⃣ CONTACT ----------
    dbEmployee.setEmail(formEmployee.getEmail());
    dbEmployee.setMobile(formEmployee.getMobile());

    // allow clear / update
    dbEmployee.setAadhaarNumber(
            formEmployee.getAadhaarNumber() == null ||
            formEmployee.getAadhaarNumber().isBlank()
                    ? null
                    : formEmployee.getAadhaarNumber()
    );

    dbEmployee.setDrivingLicenseNumber(
            formEmployee.getDrivingLicenseNumber() == null ||
            formEmployee.getDrivingLicenseNumber().isBlank()
                    ? null
                    : formEmployee.getDrivingLicenseNumber()
    );

    dbEmployee.setAddress(formEmployee.getAddress());
    dbEmployee.setCity(formEmployee.getCity());
    dbEmployee.setState(formEmployee.getState());

    // ---------- 🔟 SAVE WITH SAFETY ----------
    try {
        employeeRepository.save(dbEmployee);
        return "redirect:/employee/view";
    }
    catch (org.springframework.dao.DataIntegrityViolationException ex) {

        String msg = ex.getMostSpecificCause().getMessage();

        if (msg.contains("mobile")) {
            model.addAttribute("error", "Mobile number already exists");
        } else if (msg.contains("email")) {
            model.addAttribute("error", "Email already exists");
        } else if (msg.contains("aadhaar")) {
            model.addAttribute("error", "Aadhaar number already exists");
        } else if (msg.contains("driving")) {
            model.addAttribute("error", "Driving License number already exists");
        } else {
            model.addAttribute("error", "Duplicate data detected");
        }

        model.addAttribute("dobMax", LocalDate.now().minusYears(18));
        return "edit-employee-form";
    }
}



    // ================= DELETE =================
    @GetMapping("/admin/delete")
    public String deleteEmployeePage() {
        return "delete-employee";
    }

    @PostMapping("/admin/deleteEmployee")
    public String deleteEmployee(@RequestParam Integer employeeId) {
        employeeRepository.deleteById(employeeId);
        return "redirect:/admin/home";
    }
}
