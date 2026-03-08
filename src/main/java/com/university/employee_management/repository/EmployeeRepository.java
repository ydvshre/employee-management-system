package com.university.employee_management.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import com.university.employee_management.model.Employee;
import java.util.Optional;
import java.util.List;
import java.time.LocalDate;
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

  @Query(value = """
SELECT AUTO_INCREMENT
FROM information_schema.tables
WHERE table_name = 'employee'
AND table_schema = DATABASE()
""", nativeQuery = true)

Integer getNextEmployeeId();

List<Employee> findAllByOrderByEmployeeIdAsc();
Optional<Employee> findByEmail(String email);
Optional<Employee> findByMobile(String mobile);
Optional<Employee> findTopByOrderByEmployeeIdDesc();
Optional<Employee> findByAadhaarNumber(String aadhaarNumber);

Optional<Employee> findByDrivingLicenseNumber(String drivingLicenseNumber);

  List<Employee> findByDateOfJoiningBetween(LocalDate fromDate, LocalDate toDate);

    List<Employee> findByDepartmentIgnoreCase(String department);

    List<Employee> findByEmployeeClassIgnoreCase(String employeeClass);

    List<Employee> findByDesignationIgnoreCase(String designation);
}