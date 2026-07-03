package com.codewithme.smartqueue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,length = 100)
    private String departmentName;

    @Column(length = 225)
    private String description;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "hospital_id",nullable = false)
    private Hospital hospital;

    @PrePersist
    public void onCreate(){
        createdAt = LocalDateTime.now();
        active = true;
    }

    @PreUpdate
    public void onUpdate(){
        updatedAt = LocalDateTime.now();
    }

    // Used for soft delete. Inactive departments are hidden from normal API responses.
//    @Column(nullable = false)
//    private boolean active = true;

}
