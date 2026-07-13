package com.codewithme.smartqueue.entity;

import com.codewithme.smartqueue.enums.QueueStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "queue_tokens",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_doctor_queue_date_token",
                        columnNames = {"doctor_id", "queue_date", "token_number"}
                )
        }
)
public class QueueToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable =false)
    private Doctor doctor;

    @Column(name = "token_number", nullable = false)
    private Integer tokenNumber;

    @Column(name = "queue_date", nullable = false)
    private LocalDate queueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QueueStatus status;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "called_time")
    private LocalDateTime calledTime;

    @Column(name = "consultation_start_time")
    private LocalDateTime consultationStartTime;

    @Column(name = "consultation_end_time")
    private LocalDateTime consultationEndTime;

    @Column(nullable = false)
    private Boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }

    @PreUpdate
    public void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

}