package com.codewithme.smartqueue.dto.response;

import com.codewithme.smartqueue.enums.HospitalStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HospitalResponse {

    private Long id;
    private String hospitalName;
    private String hospitalCode;
    private String address;
    private String city;
    private String state;
    private String phoneNumber;
    private String email;
    private HospitalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
