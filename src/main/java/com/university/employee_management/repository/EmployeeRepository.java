package com.university.employee_management.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import com.university.employee_management.model.Employee;
import java.util.Optional;
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    @Query(value = """
SELECT AUTO_INCREMENT
FROM information_schema.tables
WHERE table_name = 'employee'
AND table_schema = DATABASE()
""", nativeQuery = true)
Integer getNextEmployeeId();

Optional<Employee> findTopByOrderByEmployeeIdDesc();
Optional<Employee> findByAadhaarNumber(String aadhaarNumber);

Optional<Employee> findByDrivingLicenseNumber(String drivingLicenseNumber);

}
