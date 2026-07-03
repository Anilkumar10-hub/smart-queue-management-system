package com.codewithme.smartqueue.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponse {
    private Long id;

    private String departmentName;

    private String description;

    private Long hospitalId;

    private String hospitalName;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
