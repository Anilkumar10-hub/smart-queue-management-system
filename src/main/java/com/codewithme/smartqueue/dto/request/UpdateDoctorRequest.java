package com.codewithme.smartqueue.dto.request;


import com.codewithme.smartqueue.enums.Specialization;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDoctorRequest {

    @NotBlank(message = "Doctor name is required")
    @Size(max = 100)
    private String doctorName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid Indian phone number"
    )
    private String phoneNumber;

    @NotBlank(message = "Qualification is required")
    private String qualification;

    @NotNull(message = "Specialization is required")
    private Specialization specialization;

    @NotNull(message = "Years of experience is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer yearsOfExperience;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @NotNull(message = "Consultation fee is required")
    @DecimalMin(value = "0.0")
    private BigDecimal consultationFee;

    @NotNull(message = "Department is required")
    private Long departmentId;
}
