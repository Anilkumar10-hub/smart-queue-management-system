package com.codewithme.smartqueue.controller;

import com.codewithme.smartqueue.dto.request.CreateDepartmentRequest;
import com.codewithme.smartqueue.dto.request.UpdateDepartmentRequest;
import com.codewithme.smartqueue.dto.request.UpdateHospitalRequest;
import com.codewithme.smartqueue.dto.response.DepartmentResponse;
import com.codewithme.smartqueue.dto.response.HospitalResponse;
import com.codewithme.smartqueue.service.DepartmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService){
        this.departmentService = departmentService;
    }

    @PostMapping
    public DepartmentResponse createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request){
        return departmentService.createDepartment(request);
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentResponse>> getAllDepartments(

            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 100, message = "Page size cannot exceed 100")
            int size,

            @RequestParam(defaultValue = "departmentName")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction
    ) {

        return ResponseEntity.ok(
                departmentService.getAllDepartments(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                departmentService.getDepartmentById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentRequest request){
        DepartmentResponse  departmentResponse= departmentService.updateDepartment(id,request);
        return ResponseEntity.ok(departmentResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartmenet(@PathVariable Long id) {

        String message = departmentService.deleteDepartment(id);

        return ResponseEntity.ok(message);
    }
}
