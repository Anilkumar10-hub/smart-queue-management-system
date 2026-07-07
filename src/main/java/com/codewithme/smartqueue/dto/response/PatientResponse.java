package com.codewithme.smartqueue.dto.response;

import com.codewithme.smartqueue.enums.BloodGroup;
import com.codewithme.smartqueue.enums.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class PatientResponse {

    private Long id;

    private String patientCode;

    private String patientName;

    private Gender gender;

    private LocalDate dateOfBirth;

    private BloodGroup bloodGroup;

    private String phoneNumber;

    private String email;

    private String address;

    private String emergencyContactName;

    private String emergencyContactNumber;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}