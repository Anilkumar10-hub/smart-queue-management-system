package com.codewithme.smartqueue.dto.response;

import com.codewithme.smartqueue.enums.QueueStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CheckInResponse {

    private Long queueTokenId;

    private Integer tokenNumber;

    private String patientName;

    private String doctorName;

    private QueueStatus status;

    private LocalDateTime checkInTime;

    private Integer estimatedWaitingTime;

}