package com.codewithme.smartqueue.controller;

import com.codewithme.smartqueue.dto.response.CallNextResponse;
import com.codewithme.smartqueue.dto.response.CheckInResponse;
import com.codewithme.smartqueue.service.QueueTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueTokenService queueTokenService;

    @PostMapping("/appointments/{appointmentId}/check-in")
    public ResponseEntity<CheckInResponse> checkIn(
            @PathVariable Long appointmentId) {

        return ResponseEntity.ok(queueTokenService.checkIn(appointmentId));
    }

    @PostMapping("doctors/{doctorId}/call-next")
    public ResponseEntity<CallNextResponse> callNextPatient(
            @PathVariable Long doctorId){

        return ResponseEntity.ok(
                queueTokenService.callNextPatient(doctorId)
        );
    }


}