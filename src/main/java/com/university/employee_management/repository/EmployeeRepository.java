package com.university.employee_management.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import com.university.employee_management.model.Employee;
import java.util.Optional;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

   @Query(
  value = "SELECT nextval(pg_get_serial_sequence('employee', 'employee_id'))",
  nativeQuery = true
)
Integer getNextEmployeeId();
List<Employee> findAllByOrderByEmployeeIdAsc();

Optional<Employee> findTopByOrderByEmployeeIdDesc();
Optional<Employee> findByAadhaarNumber(String aadhaarNumber);

Optional<Employee> findByDrivingLicenseNumber(String drivingLicenseNumber);

}
