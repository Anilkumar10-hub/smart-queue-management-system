package com.codewithme.smartqueue.repository;

import com.codewithme.smartqueue.entity.Appointment;
import com.codewithme.smartqueue.entity.Doctor;
import com.codewithme.smartqueue.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByIdAndActiveTrue(Long id);

    Page<Appointment> findByActiveTrue(Pageable pageable);

    boolean existsByDoctorAndAppointmentDateAndAppointmentTime(
            Doctor doctor,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );

    boolean existsByPatientAndAppointmentDateAndAppointmentTime(
            Patient patient,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );

    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndIdNot(
            Doctor doctor,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            Long id
    );

    boolean existsByPatientAndAppointmentDateAndAppointmentTimeAndIdNot(
            Patient patient,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            Long id
    );

    Page<Appointment> findByActiveTrueAndAppointmentDate(
            LocalDate appointmentDate,
            Pageable pageable
    );
}