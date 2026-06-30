package com.codewithme.smartqueue.controller;

import com.codewithme.smartqueue.dto.request.CreateHospitalRequest;
import com.codewithme.smartqueue.dto.request.UpdateHospitalRequest;
import com.codewithme.smartqueue.dto.response.HospitalResponse;
import com.codewithme.smartqueue.service.HospitalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@Validated
@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;


    public HospitalController(HospitalService hospitalService){
        this.hospitalService=hospitalService;
    }

    @PostMapping
    public HospitalResponse createHospital(
           @Valid @RequestBody CreateHospitalRequest request){
        return hospitalService.createHospital(request);
    }
    @GetMapping
    public ResponseEntity<Page<HospitalResponse>> getAllHospitals(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 100, message = "Page size cannot exceed 100")
            int size,

            @RequestParam(defaultValue = "hospitalName")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction,

            @RequestParam(required = false)
            String city
    ){


        return ResponseEntity.ok(
                hospitalService.getAllHospitals(
                        page,
                        size,
                        sortBy,
                        direction,
                        city)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HospitalResponse> getHospitalById(@PathVariable Long id){
        HospitalResponse hospital = hospitalService.getHospitalById(id);
        return ResponseEntity.ok(hospital);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HospitalResponse> updateHospital(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHospitalRequest request){
        HospitalResponse hospitalResponse = hospitalService.updateHospital(id,request);
        return ResponseEntity.ok(hospitalResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteHospital(@PathVariable Long id) {

        String message = hospitalService.deleteHospital(id);

        return ResponseEntity.ok(message);
    }
}

