package com.codewithme.smartqueue.entity;

import com.codewithme.smartqueue.enums.HospitalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "hospitals")
@Getter
@Setter
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @NotBlank
    @Column(
            name = "hospital_name",
            nullable = false,
            length = 100
    )
    private String hospitalName;

    @NotBlank
    @Column(
            name = "hospital_code",
            nullable = false,
            unique = true,
            length = 20,
            updatable = false
    )
    private String hospitalCode;

    @NotBlank
    @Column(
            name = "address",
            nullable = false,
            length = 250
    )
    private String address;

    @NotBlank
    @Column(
            name = "city",
            nullable = false,
            length = 100
    )
    private String city;

    @NotBlank
    @Column(
            name = "state",
            nullable = false,
            length = 100
    )
    private String state;

    @NotBlank
    @Column(
            name = "phone_number",
            nullable = false,
            length = 20
    )
    private String phoneNumber;

    @NotBlank
    @Column(
            name = "email",
            nullable = false,
            length = 50
    )
    private String email;

    @Enumerated(EnumType.STRING)

    @Column(nullable = false)
    private HospitalStatus status = HospitalStatus.PENDING_APPROVAL;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Used for soft delete. Inactive hospitals are hidden from normal API responses.
    @Column(nullable = false)
    private boolean active = true;
}
