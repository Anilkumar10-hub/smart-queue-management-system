package com.codewithme.smartqueue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CreateDepartmentRequest {

    @NotBlank
    private String departmentName;

    @Size(max = 225)
    private String description;

    @NotNull
    private Long hospitalId;

}
