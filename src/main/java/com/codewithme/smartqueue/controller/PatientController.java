package com.codewithme.smartqueue.controller;

import com.codewithme.smartqueue.dto.request.CreatePatientRequest;
import com.codewithme.smartqueue.dto.request.UpdatePatientRequest;
import com.codewithme.smartqueue.dto.response.PatientResponse;
import com.codewithme.smartqueue.service.PatientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public PatientResponse createPatient(
            @Valid @RequestBody CreatePatientRequest request) {

        return patientService.createPatient(request);
    }

    @GetMapping
    public Page<PatientResponse> getAllPatients(

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size,

            @RequestParam(defaultValue = "patientName")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        return patientService.getAllPatients(
                page,
                size,
                sortBy,
                direction
        );
    }

    @GetMapping("/{id}")
    public PatientResponse getPatientById(
            @PathVariable Long id) {

        return patientService.getPatientById(id);
    }

    @PutMapping("/{id}")
    public PatientResponse updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest request) {

        return patientService.updatePatient(id, request);
    }

    @DeleteMapping("/{id}")
    public String deletePatient(
            @PathVariable Long id) {

        return patientService.deletePatient(id);
    }

}