package com.mindex.challenge.data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.util.Date;

public class Compensation {
    @NotBlank(message = "Employee ID is required")
    private String employeeId;
    @Positive(message = "Salary must be a positive number")
    private double salary;
    @NotNull(message = "Effective date is required")
    @FutureOrPresent(message = "Effective date must be in the past, present, or future")
    private Date effectiveDate;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
