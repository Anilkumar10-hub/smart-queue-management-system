package com.codewithme.smartqueue.dto.response;
import com.codewithme.smartqueue.enums.DoctorAvailabilityStatus;
import com.codewithme.smartqueue.enums.Specialization;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {

    private Long id;

    private String doctorCode;

    private String doctorName;

    private String email;

    private String phoneNumber;

    private String qualification;

    private Specialization specialization;

    private Integer yearsOfExperience;

    private LocalDate joiningDate;

    private BigDecimal consultationFee;

    private DoctorAvailabilityStatus availabilityStatus;

    private boolean active;

    private Long departmentId;

    private String departmentName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}