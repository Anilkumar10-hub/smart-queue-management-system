package com.codewithme.smartqueue.service;

import com.codewithme.smartqueue.dto.response.CallNextResponse;
import com.codewithme.smartqueue.dto.response.CheckInResponse;
import com.codewithme.smartqueue.entity.Appointment;
import com.codewithme.smartqueue.entity.Doctor;
import com.codewithme.smartqueue.entity.QueueToken;
import com.codewithme.smartqueue.enums.AppointmentStatus;
import com.codewithme.smartqueue.enums.QueueStatus;
import com.codewithme.smartqueue.exception.InvalidOperationException;
import com.codewithme.smartqueue.exception.ResourceNotFoundException;
import com.codewithme.smartqueue.repository.AppointmentRepository;
import com.codewithme.smartqueue.repository.DoctorRepository;
import com.codewithme.smartqueue.repository.QueueTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QueueTokenService {

    private final QueueTokenRepository queueTokenRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public QueueTokenService(QueueTokenRepository queueTokenRepository,
                             AppointmentRepository appointmentRepository,
                             DoctorRepository doctorRepository){
        this.appointmentRepository = appointmentRepository;
        this.queueTokenRepository = queueTokenRepository;
        this.doctorRepository = doctorRepository;
    }


    private Appointment getActiveAppointment(Long appointmentId) {
        return appointmentRepository
                .findByIdAndActiveTrue(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with ID: " + appointmentId
                ));
    }

    private void validateAppointmentStatus(Appointment appointment){
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new InvalidOperationException(
                    "Only BOOKED appointments can be checked in. Current status: "
                            + appointment.getStatus()
            );
        }
    }

    private void validateAppointmentDate(Appointment appointment) {
        if (!appointment.getAppointmentDate().equals(LocalDate.now())) {
            throw new InvalidOperationException(
                    "Patient can only check in on the appointment date. Appointment date: "
                            + appointment.getAppointmentDate()
            );
        }
    }

    private void validateAlreadyCheckedIn(Appointment appointment) {
        if (queueTokenRepository.findByAppointmentAndActiveTrue(appointment).isPresent()) {
            throw new InvalidOperationException(
                    "This appointment has already been checked in."
            );
        }
    }

    private Integer generateNextToken(Doctor doctor) {

        Optional<QueueToken> lastToken =
                queueTokenRepository.findTopByDoctorAndQueueDateOrderByTokenNumberDesc(
                        doctor,
                        LocalDate.now()
                );

        return lastToken
                .map(queueToken -> queueToken.getTokenNumber() + 1)
                .orElse(1);
    }

    private QueueToken createQueueToken(Appointment appointment, Integer tokenNumber) {

        QueueToken queueToken = new QueueToken();

        LocalDateTime now = LocalDateTime.now();

        queueToken.setAppointment(appointment);
        queueToken.setPatient(appointment.getPatient());
        queueToken.setDoctor(appointment.getDoctor());

        queueToken.setTokenNumber(tokenNumber);
        queueToken.setQueueDate(now.toLocalDate());

        queueToken.setStatus(QueueStatus.WAITING);
        queueToken.setCheckInTime(now);

        return queueToken;
    }

    private CheckInResponse mapToCheckInResponse(QueueToken queueToken) {

        CheckInResponse response = new CheckInResponse();

        response.setQueueTokenId(queueToken.getId());
        response.setTokenNumber(queueToken.getTokenNumber());

        response.setPatientName(queueToken.getPatient().getPatientName());
        response.setDoctorName(queueToken.getDoctor().getDoctorName());

        response.setStatus(queueToken.getStatus());

        response.setCheckInTime(queueToken.getCheckInTime());

        // Version 1
        response.setEstimatedWaitingTime(null);

        return response;
    }

    @Transactional
    public CheckInResponse checkIn(Long appointmentId) {

        Appointment appointment = getActiveAppointment(appointmentId);

        validateAppointmentStatus(appointment);

        validateAppointmentDate(appointment);

        validateAlreadyCheckedIn(appointment);

        int tokenNumber = generateNextToken(appointment.getDoctor());

        QueueToken queueToken = createQueueToken(appointment, tokenNumber);

        appointment.setStatus(AppointmentStatus.CHECKED_IN);

        appointmentRepository.save(appointment);

        queueTokenRepository.save(queueToken);

        return mapToCheckInResponse(queueToken);
    }

    //
    private Doctor getActiveDoctor(Long doctorId){
        return doctorRepository.findByIdAndActiveTrue(doctorId)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Doctor not found with ID :"+doctorId
                ));
    }

    private static final List<QueueStatus> DOCTOR_BUSY_STATUSES = List.of(
            QueueStatus.CALLED,
            QueueStatus.IN_CONSULTATION
    );


    private void validateDoctorCanCallNext(Doctor doctor) {

        boolean doctorBusy =
                queueTokenRepository.existsByDoctorAndQueueDateAndStatusInAndActiveTrue(
                        doctor,
                        LocalDate.now(),
                        DOCTOR_BUSY_STATUSES
                );

        if (doctorBusy) {
            throw new InvalidOperationException(
                    "Doctor is already attending another patient."
            );
        }
    }

    private QueueToken getNextWaitingToken(Doctor doctor) {

        return queueTokenRepository
                .findFirstByDoctorAndQueueDateAndStatusOrderByTokenNumberAsc(
                        doctor,
                        LocalDate.now(),
                        QueueStatus.WAITING
                )
                .orElseThrow(() -> new InvalidOperationException(
                        "No patients are waiting in the queue."
                ));
    }

    private void callQueueToken(QueueToken queueToken) {

        queueToken.setStatus(QueueStatus.CALLED);
        queueToken.setCalledTime(LocalDateTime.now());

        queueTokenRepository.save(queueToken);
    }

    private CallNextResponse mapToCallNextResponse(QueueToken queueToken) {

        CallNextResponse response = new CallNextResponse();

        response.setQueueTokenId(queueToken.getId());
        response.setAppointmentCode(
                queueToken.getAppointment().getAppointmentCode());

        response.setPatientCode(
                queueToken.getPatient().getPatientCode());

        response.setPatientName(
                queueToken.getPatient().getPatientName());

        response.setDoctorName(
                queueToken.getDoctor().getDoctorName());

        response.setTokenNumber(
                queueToken.getTokenNumber());

        response.setStatus(
                queueToken.getStatus());

        response.setCalledTime(
                queueToken.getCalledTime());

        return response;
    }
    @Transactional
    public CallNextResponse callNextPatient(Long doctorId) {

        Doctor doctor = getActiveDoctor(doctorId);

        validateDoctorCanCallNext(doctor);

        QueueToken queueToken = getNextWaitingToken(doctor);

        callQueueToken(queueToken);
        return mapToCallNextResponse(queueToken);
    }

}
