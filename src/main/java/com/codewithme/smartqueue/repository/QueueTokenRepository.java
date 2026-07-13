package com.codewithme.smartqueue.repository;

import com.codewithme.smartqueue.entity.Appointment;
import com.codewithme.smartqueue.entity.Doctor;
import com.codewithme.smartqueue.entity.QueueToken;
import com.codewithme.smartqueue.enums.QueueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface QueueTokenRepository extends JpaRepository<QueueToken,Long> {

    Optional<QueueToken> findByAppointmentAndActiveTrue(Appointment appointment);

    //next token
    Optional<QueueToken> findTopByDoctorAndQueueDateOrderByTokenNumberDesc(
            Doctor doctor,
            LocalDate queueDate
    );

    //call for next patient
    Optional<QueueToken> findFirstByDoctorAndQueueDateAndStatusOrderByTokenNumberAsc(
            Doctor doctor,
            LocalDate queueDate,
            QueueStatus status
    );

    Page<QueueToken> findByDoctorAndQueueDate(
            Doctor doctor,
            LocalDate queueDate,
            Pageable pageable
    );

    boolean existsByDoctorAndQueueDateAndStatusInAndActiveTrue(
            Doctor doctor,
            LocalDate queueDate,
            Collection<QueueStatus> statuses
    );
}
