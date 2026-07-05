package com.codewithme.smartqueue.controller;

import com.codewithme.smartqueue.dto.request.CreateDoctorRequest;
import com.codewithme.smartqueue.dto.request.UpdateDoctorRequest;
import com.codewithme.smartqueue.dto.response.DoctorResponse;
import com.codewithme.smartqueue.service.DoctorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService){
        this.doctorService = doctorService;
    }

    @PostMapping
    public DoctorResponse createDoctor(
            @Valid @RequestBody CreateDoctorRequest request){
        return doctorService.createDoctor(request);
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> getAllDoctors(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 100, message = "Page size cannot exceed 100")
            int size,

            @RequestParam(defaultValue = "doctorName")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction
    ){
        return ResponseEntity.ok(
                doctorService.getAllDoctors(
                        page,
                        size,
                        sortBy,
                        direction
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(
            @PathVariable Long id){

        return ResponseEntity.ok(
                doctorService.getDoctorById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDoctorRequest request){
        DoctorResponse doctorResponse = doctorService.updateDoctor(id,request);
        return ResponseEntity.ok(doctorResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctorById(
            @PathVariable Long id){

        return  ResponseEntity.ok(
                doctorService.deleteDoctorById(id)
        );
    }
}
