package com.codewithme.smartqueue.service;


import com.codewithme.smartqueue.dto.request.CreateAppointmentRequest;
import com.codewithme.smartqueue.dto.response.AppointmentResponse;
import com.codewithme.smartqueue.entity.Appointment;
import com.codewithme.smartqueue.entity.Department;
import com.codewithme.smartqueue.entity.Doctor;
import com.codewithme.smartqueue.entity.Patient;
import com.codewithme.smartqueue.exception.DuplicateResourceException;
import com.codewithme.smartqueue.exception.InvalidOperationException;
import com.codewithme.smartqueue.exception.ResourceNotFoundException;
import com.codewithme.smartqueue.repository.AppointmentRepository;
import com.codewithme.smartqueue.repository.DepartmentRepository;
import com.codewithme.smartqueue.repository.DoctorRepository;
import com.codewithme.smartqueue.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional

public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              DepartmentRepository departmentRepository){
        this.appointmentRepository=appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.patientRepository = patientRepository;
    }


    // Allowed sorting fields
    List<String> allowedSortFields = List.of(
            "appointmentDate",
            "appointmentTime",
            "createdAt",
            "appointmentCode"
    );

    //DTO mapping
    private Appointment mapToAppointment(CreateAppointmentRequest request,
                                         Patient patient,
                                         Doctor doctor,
                                         Department department) {

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDepartment(department);

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());

        appointment.setReasonForVisit(request.getReasonForVisit());
        appointment.setNotes(request.getNotes());

        return appointment;
    }

    //response
    private AppointmentResponse mapToResponse(Appointment appointment) {

        AppointmentResponse response = new AppointmentResponse();

        response.setId(appointment.getId());
        response.setAppointmentCode(appointment.getAppointmentCode());

        response.setPatientId(appointment.getPatient().getId());
        response.setPatientName(appointment.getPatient().getPatientName());

        response.setDoctorId(appointment.getDoctor().getId());
        response.setDoctorName(appointment.getDoctor().getDoctorName());

        response.setDepartmentId(appointment.getDepartment().getId());
        response.setDepartmentName(appointment.getDepartment().getDepartmentName());

        response.setAppointmentDate(appointment.getAppointmentDate());
        response.setAppointmentTime(appointment.getAppointmentTime());

        response.setStatus(appointment.getStatus());

        response.setReasonForVisit(appointment.getReasonForVisit());
        response.setNotes(appointment.getNotes());

        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());

        return response;
    }

    //validating patient
    private Patient getActivePatient(Long patientId){
        return patientRepository.findByIdAndActiveTrue(patientId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Patient not found with id :" + patientId));
    }

    //validating doctor
    private Doctor getActiveDoctor(Long doctorId){
        return doctorRepository.findByIdAndActiveTrue(doctorId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Doctor not found with id :" + doctorId));
    }
    //validating department
    private Department getActiveDepartment(Long departmentId){
        return departmentRepository.findByIdAndActiveTrue(departmentId)
                .orElseThrow(()->
                        new ResourceNotFoundException("Department not found with id :" + departmentId));
    }

    //validating Active appointment
    private Appointment getActiveAppointment(Long appointmentId){
        return appointmentRepository.findByIdAndActiveTrue(appointmentId)
                .orElseThrow(()->
                        new ResourceNotFoundException(
                                "Appointment not found with id : " + appointmentId
                        ));
    }

    //validate doctor and department
    private void validateDoctorDepartment(Doctor doctor , Department department){
        if(!doctor.getDepartment().getId().equals(department.getId())){
            throw new InvalidOperationException(
                    "Selected doctor does not belong to the selected department."
            );
        }
    }

    //validating appointment date & time
    private void validateAppointmentDate(LocalDate appointmentDate,
                                         LocalTime appointmentTime){
        LocalDate today = LocalDate.now();
        LocalTime cuurentTime = LocalTime.now();

        //cannot book for past data
        if(appointmentDate.isBefore(today)){
            throw new InvalidOperationException(
                    "Appointment date cannot be in past"
            );
        }

        //same day booking
        if(appointmentDate.equals(today) &&
            appointmentTime.isBefore(cuurentTime)){
            throw new InvalidOperationException(
                    "Appointment Time cannot be past");
        }
    }

    //validate Doctor availability
    private void validateDoctorAvailability(CreateAppointmentRequest request,
                                            Doctor doctor) {

        boolean exists = appointmentRepository
                .existsByDoctorAndAppointmentDateAndAppointmentTime(
                        doctor,
                        request.getAppointmentDate(),
                        request.getAppointmentTime()
                );

        if (exists) {
            throw new DuplicateResourceException(
                    "Doctor is already booked for the selected date and time."
            );
        }
    }

    //validate patient availability
    private void validatePatientAvailability(CreateAppointmentRequest request,
                                             Patient patient) {

        boolean exists = appointmentRepository
                .existsByPatientAndAppointmentDateAndAppointmentTime(
                        patient,
                        request.getAppointmentDate(),
                        request.getAppointmentTime()
                );

        if (exists) {
            throw new DuplicateResourceException(
                    "Patient already has an appointment at the selected date and time."
            );
        }
    }

    //appointment code
    private String generateAppointmentCode(Long id) {
        return String.format("APT%03d", id);
    }


    //create appointment
    public AppointmentResponse createAppointment(CreateAppointmentRequest request){
        Patient patient = getActivePatient(request.getPatientId());

        Doctor doctor = getActiveDoctor(request.getDoctorId());

        Department department = getActiveDepartment(request.getDepartmentId());

        validateDoctorDepartment(doctor, department);

        validateAppointmentDate(
                request.getAppointmentDate(),
                request.getAppointmentTime());

        validateDoctorAvailability(request, doctor);

        validatePatientAvailability(request, patient);

        Appointment appointment = mapToAppointment(request,patient,doctor,department);

        appointment = appointmentRepository.save(appointment);

        appointment.setAppointmentCode(generateAppointmentCode(appointment.getId()));

        appointment = appointmentRepository.save(appointment);

        return mapToResponse(appointment);
    }

    //get appointment by id
    public AppointmentResponse getAppointmentById(Long appointmentId){
        Appointment appointment = getActiveAppointment(appointmentId);
        return mapToResponse(appointment);
    }

    //get all appointments
    public Page<AppointmentResponse> getAllAppointments(
            int page,
            int size,
            String sortBy,
            String direction,
            LocalDate appointmentDate) {

        // Validate sorting direction
        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {

            throw new InvalidOperationException(
                    "Direction must be either 'asc' or 'desc'.");
        }



        if (!allowedSortFields.contains(sortBy)) {
            throw new InvalidOperationException(
                    "Invalid sort field: " + sortBy);
        }

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Appointment> appointments;

        if (appointmentDate != null) {

            appointments = appointmentRepository
                    .findByActiveTrueAndAppointmentDate(
                            appointmentDate,
                            pageable
                    );

        } else {

            appointments = appointmentRepository
                    .findByActiveTrue(pageable);

        }

        return appointments.map(this::mapToResponse);
    }


}
