package com.codewithme.smartqueue.dto.response;

import com.codewithme.smartqueue.enums.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentResponse {

    private Long id;

    private String appointmentCode;

    private Long patientId;

    private String patientName;

    private Long doctorId;

    private String doctorName;

    private Long departmentId;

    private String departmentName;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private AppointmentStatus status;

    private String reasonForVisit;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}