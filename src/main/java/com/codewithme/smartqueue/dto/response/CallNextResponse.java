package com.codewithme.smartqueue.dto.response;

import com.codewithme.smartqueue.enums.QueueStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CallNextResponse {
    Long queueTokenId;
    String appointmentCode;
    String patientCode;
    String patientName;
    String doctorName;
    Integer tokenNumber;
    QueueStatus status;
    LocalDateTime calledTime;
}
