package com.university.employee_management.model;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;

    // ================= DEPARTMENT =================
    private String title;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Only alphabets allowed")
    private String firstName;

    @Pattern(regexp = "^[A-Za-z ]*$")
    private String middleName;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z ]+$")
    private String lastName;

    private String department;
    private String designation;
    private String payScale;
    private Double basicSalary;
    private String employeeClass;
    private String natureOfAppointment;
   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
private LocalDate dateOfJoining;
   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
private LocalDate contractStartDate;

@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
private LocalDate contractEndDate;

@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
private LocalDate confirmationDate;

    // ================= PERSONAL =================
    @Pattern(regexp = "^[A-Za-z ]+$")
    private String fatherName;

    @Pattern(regexp = "^[A-Za-z ]+$")
    private String motherName;

    private String maritalStatus;

    @Pattern(regexp = "^[A-Za-z ]*$")
    private String spouseName;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
@Past(message = "Date of birth must be in the past")
private LocalDate dateOfBirth;

@AssertTrue(message = "Employee must be at least 18 years old")
public boolean isAdult() {
    if (dateOfBirth == null) {
        return false;
    }
    return Period.between(dateOfBirth, LocalDate.now()).getYears() >= 18;
}

    private String gender;


@Pattern(
    regexp = "^$|^[0-9]{12}$",
    message = "Aadhaar must be exactly 12 digits"
)
@Column(unique = true, nullable = true)
private String aadhaarNumber;



@Pattern(
    regexp = "^$|^[A-Za-z0-9]{16}$",
    message = "License must be 16 alphanumeric characters"
)
@Column(unique = true, nullable = true)
private String drivingLicenseNumber;


@AssertTrue(message = "Either Aadhaar OR Driving License is required")
public boolean isIdentityProvided() {

    boolean hasAadhaar =
            aadhaarNumber != null && !aadhaarNumber.trim().isEmpty();

    boolean hasLicense =
            drivingLicenseNumber != null && !drivingLicenseNumber.trim().isEmpty();

    return hasAadhaar || hasLicense;
}




    private String category;

    @Email(message = "Invalid email format")
@Pattern(
    regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
    message = "Email must contain letters, numbers, '.', '@' and a valid domain"
)
@Column(unique = true, nullable = true)
private String email;


    @Size(min = 10, max = 10)
    @Pattern(regexp = "[0-9]+")
    @Column(unique = true)
    private String mobile;

    private String address;
    private String city;
    private String state;

   

    public Integer getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public String getDesignation() {
        return designation;
    }
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    public String getPayScale() {
        return payScale;
    }
    public void setPayScale(String payScale) {
        this.payScale = payScale;
    }
    public Double getBasicSalary() {
        return basicSalary;
    }
    public void setBasicSalary(Double basicSalary) {
        this.basicSalary = basicSalary;
    }
    public String getEmployeeClass() {
        return employeeClass;
    }
    public void setEmployeeClass(String employeeClass) {
        this.employeeClass = employeeClass;
    }
    public String getNatureOfAppointment() {
        return natureOfAppointment;
    }
    public void setNatureOfAppointment(String natureOfAppointment) {
        this.natureOfAppointment = natureOfAppointment;
    }
    public LocalDate getDateOfJoining() {
        return dateOfJoining;
    }
    public void setDateOfJoining(LocalDate dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }
    public LocalDate getContractStartDate() {
        return contractStartDate;
    }
    public void setContractStartDate(LocalDate contractStartDate) {
        this.contractStartDate = contractStartDate;
    }
    public LocalDate getContractEndDate() {
        return contractEndDate;
    }
    public void setContractEndDate(LocalDate contractEndDate) {
        this.contractEndDate = contractEndDate;
    }
    public LocalDate getConfirmationDate() {
        return confirmationDate;
    }
    public void setConfirmationDate(LocalDate confirmationDate) {
        this.confirmationDate = confirmationDate;
    }
    public String getFatherName() {
        return fatherName;
    }
    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }
    public String getMotherName() {
        return motherName;
    }
    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }
    public String getMaritalStatus() {
        return maritalStatus;
    }
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    public String getSpouseName() {
        return spouseName;
    }
    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getAadhaarNumber() {
        return aadhaarNumber;
    }
    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }
    public String getDrivingLicenseNumber() {
    return drivingLicenseNumber;
}

public void setDrivingLicenseNumber(String drivingLicenseNumber) {
    this.drivingLicenseNumber = drivingLicenseNumber;
}

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    // getters & setters (VS Code can auto-generate)
}
