package com.codewithme.smartqueue.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHospitalRequest {

    @NotBlank(message = "Hospital name is required")
    @Size(max = 100, message = "Hospital name cannot exceed 100 characters")
    private String hospitalName;

    @NotBlank(message = "Hospital code is required")
    @Size(max = 20, message = "Hospital code cannot exceed 20 characters")
    private String hospitalCode;

    @NotBlank(message = "Address is required")
    @Size(max = 250, message = "Address cannot exceed 250 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100)
    private String state;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Phone number must be a valid 10-digit Indian mobile number"
    )
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 50)
    private String email;
}
