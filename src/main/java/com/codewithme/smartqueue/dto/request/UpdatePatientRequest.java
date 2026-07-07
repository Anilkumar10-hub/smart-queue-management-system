package com.codewithme.smartqueue.dto.request;

import com.codewithme.smartqueue.enums.BloodGroup;
import com.codewithme.smartqueue.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdatePatientRequest {

    @NotBlank(message = "Patient name is required")
    @Size(max = 100)
    private String patientName;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Emergency contact name is required")
    private String emergencyContactName;

    @NotBlank(message = "Emergency contact number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",
            message = "Invalid emergency contact number")
    private String emergencyContactNumber;

}