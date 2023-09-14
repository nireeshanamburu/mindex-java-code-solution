package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.FieldError;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CompensationService compensationService;

    @PostMapping("/employee")
    public Employee create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee);

        return employeeService.create(employee);
    }

    @GetMapping("/employee/{id}")
    public Employee read(@PathVariable String id) {
        LOG.debug("Received employee create request for id [{}]", id);

        return employeeService.read(id);
    }

    @PutMapping("/employee/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee create request for id [{}] and employee [{}]", id, employee);

        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }

    /**
     * Added postman collection to test these end points (src/main/resources/postman)
     * @param id
     * @return
     */
    @GetMapping("employee/reportingStructure/{id}")
    public ResponseEntity<ReportingStructure> getReportingStructure(@PathVariable String id) {
        Employee employee = employeeService.read(id);

        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        // Calculate the numberOfReports
        int numberOfReports = calculateNumberOfReports(employee);

        // Create a ReportingStructure object and set its fields
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(numberOfReports);

        return ResponseEntity.ok(reportingStructure);
    }

    /**
     * This Method calculates number of reports for a given employee
     *
     * @param employee
     * @return
     */
    private int calculateNumberOfReports(Employee employee) {
        // Recursive function for exploring the employee hierarchy
        Set<String> uniqueReports = new HashSet<>();
        calculateReports(employee, uniqueReports);
        return uniqueReports.size();
    }

    /**
     * This method uses java streams api to find uniqueReports and calls calculateReports recursively to find reports for given employee
     * @param employee
     * @param uniqueReports
     */
    private void calculateReports(Employee employee, Set<String> uniqueReports) {
        List<Employee> directReports = employee.getDirectReports();
        if (!CollectionUtils.isEmpty(directReports)) {
            directReports.stream()
                    .filter(e -> e != null && !uniqueReports.contains(e.getEmployeeId())) // Filter out already added reports
                    .forEach(e -> {
                        uniqueReports.add(e.getEmployeeId());
                        Employee report = employeeService.read(e.getEmployeeId());
                        if (report != null) {
                            calculateReports(report, uniqueReports);
                        }
                    });
        }
    }

    /**
     * Added postman collection to test these end points (src/main/resources/postman)
     * @param compensation
     * @param result
     * @return
     */
    @PostMapping("employee/compensation")
    public ResponseEntity<?> createCompensation(@RequestBody @Valid Compensation compensation, BindingResult result) {
        if (result.hasErrors()) {
            // If there are validation errors, return a bad request response with error details
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }
        // Check if employee exists for given employee id
        Employee employee = employeeService.read(compensation.getEmployeeId());
        if(employee == null){
            // if employee doesn't exist then throw an error.
            Map<String, String> errors = new HashMap<>();
            errors.put("ErrorMessage","Employee id is Invalid");
            return ResponseEntity.badRequest().body(errors);
        }

        Compensation createdCompensation = compensationService.create(compensation);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCompensation);
    }

    /**
     * Added postman collection to test these end points (src/main/resources/postman)
     * @param employeeId
     * @return
     */
    @GetMapping("employee/compensation/{employeeId}")
    public ResponseEntity<Compensation> getCompensationByEmployeeId(@PathVariable String employeeId) {
        Compensation compensation = compensationService.read(employeeId);
        if (compensation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(compensation);
    }
}
