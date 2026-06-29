package com.codewithme.smartqueue.controller;

import com.codewithme.smartqueue.dto.request.CreateHospitalRequest;
import com.codewithme.smartqueue.dto.request.UpdateHospitalRequest;
import com.codewithme.smartqueue.dto.response.HospitalResponse;
import com.codewithme.smartqueue.service.HospitalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<HospitalResponse>> getAllHospitals(){

        List<HospitalResponse> hospitals = hospitalService.getAllHospitals();
        return ResponseEntity.ok(hospitals);
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

