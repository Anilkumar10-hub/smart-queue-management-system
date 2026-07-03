package com.codewithme.smartqueue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDepartmentRequest {

    @NotBlank(message = "Department name is required")
    private String departmentName;

    private String description;

    @NotNull(message = "Hospital ID is required")
    private Long hospitalId;
}