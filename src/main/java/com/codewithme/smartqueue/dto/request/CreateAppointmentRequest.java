package com.codewithme.smartqueue.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CreateAppointmentRequest {

    @NotNull(message = "Patient Id is required.")
    private Long patientId;

    @NotNull(message = "Doctor Id is required.")
    private Long doctorId;

    @NotNull(message = "Department Id is required.")
    private Long departmentId;

    @NotNull(message = "Appointment Date is required.")
    @FutureOrPresent(message = "Appointment date must be in the future or present.")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment Time is required.")
    private LocalTime appointmentTime;

    @NotBlank(message = "Reason for visit is required.")
    @Size(max = 255)
    private String reasonForVisit;

    @Size(max = 500)
    private String notes;
}