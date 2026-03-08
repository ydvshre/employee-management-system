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

    // ================= 1️⃣ BASIC BEAN VALIDATION =================
    if (result.hasErrors()) {
        return "add-employee";
    }

    // ================= 2️⃣ NORMALIZE EMPTY STRINGS =================
    // VERY IMPORTANT (prevents UNIQUE chaos)

    if (employee.getAadhaarNumber() != null &&
        employee.getAadhaarNumber().isBlank()) {
        employee.setAadhaarNumber(null);
    }

    if (employee.getDrivingLicenseNumber() != null &&
        employee.getDrivingLicenseNumber().isBlank()) {
        employee.setDrivingLicenseNumber(null);
    }

    // ================= 3️⃣ AT LEAST ONE ID REQUIRED =================
    if (employee.getAadhaarNumber() == null &&
        employee.getDrivingLicenseNumber() == null) {

        result.reject(
            "identity",
            "Either Aadhaar OR Driving License must be provided"
        );
        return "add-employee";
    }

    // ================= 4️⃣ UNIQUE AADHAAR CHECK =================
    if (employee.getAadhaarNumber() != null) {
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

    // ================= 5️⃣ UNIQUE LICENSE CHECK =================
    if (employee.getDrivingLicenseNumber() != null) {
        if (employeeRepository
                .findByDrivingLicenseNumber(
                        employee.getDrivingLicenseNumber())
                .isPresent()) {

            result.rejectValue(
                "drivingLicenseNumber",
                "error.employee",
                "Driving License number already exists"
            );
        }
    }

    // ================= 6 UNIQUE EMAIL CHECK (MISSING BEFORE) =================
    if (employee.getEmail() != null &&
        !employee.getEmail().isBlank()) {

        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            result.rejectValue(
                    "email",
                    "error.employee",
                    "Email already exists"
            );
        }
    }

    // ================= 7 UNIQUE MOBILE CHECK (MISSING BEFORE) =================
    if (employee.getMobile() != null &&
        !employee.getMobile().isBlank()) {

        if (employeeRepository.findByMobile(employee.getMobile()).isPresent()) {
            result.rejectValue(
                    "mobile",
                    "error.employee",
                    "Mobile number already exists"
            );
        }
    }

    // ================= 8 RETURN IF ANY ERROR =================
    if (result.hasErrors()) {
        return "add-employee";
    }

    // ================= 9 SAVE =================
    try {
        Employee savedEmployee = employeeRepository.save(employee);
        Integer id = savedEmployee.getEmployeeId();
        return "redirect:/employee/view?highlightId=" + id;
    }
    catch (org.springframework.dao.DataIntegrityViolationException ex) {

    String msg = "";

    if (ex.getMostSpecificCause() != null) {
        msg = ex.getMostSpecificCause().getMessage().toLowerCase();
    }

    if (msg.contains("mobile")) {
        model.addAttribute("error", "Mobile number already exists");
    }
    else if (msg.contains("email")) {
        model.addAttribute("error", "Email already exists");
    }
    else if (msg.contains("aadhaar")) {
        model.addAttribute("error", "Aadhaar number already exists");
    }
    else if (msg.contains("driving")) {
        model.addAttribute("error", "Driving License number already exists");
    }
    else {
        model.addAttribute(
            "error",
            "Duplicate value found in a unique field. Please check your input."
        );
    }

    return "add-employee";
}

}




    // ================= VIEW EMPLOYEES =================
   @GetMapping("/employee/view")
public String viewEmployees(
        @RequestParam(value = "role", defaultValue = "admin") String role,
        @RequestParam(value = "highlightId", required = false) Integer highlightId,
        Model model
) {
    model.addAttribute(
        "employees",
        employeeRepository.findAllByOrderByEmployeeIdAsc()
    );

    model.addAttribute("highlightId", highlightId);

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
                model.addAttribute("employee", emp) ;
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
