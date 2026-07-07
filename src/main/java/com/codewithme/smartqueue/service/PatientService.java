package com.codewithme.smartqueue.service;

import com.codewithme.smartqueue.dto.request.CreatePatientRequest;
import com.codewithme.smartqueue.dto.request.UpdatePatientRequest;
import com.codewithme.smartqueue.dto.response.PatientResponse;
import com.codewithme.smartqueue.entity.Patient;
import com.codewithme.smartqueue.exception.DuplicateResourceException;
import com.codewithme.smartqueue.exception.InvalidOperationException;
import com.codewithme.smartqueue.exception.ResourceNotFoundException;
import com.codewithme.smartqueue.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    private static final List<String> allowedSortFields = List.of(
            "patientName",
            "patientCode",
            "createdAt",
            "dateOfBirth"
    );

    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request) {

        if (patientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Patient already exists with email : " + request.getEmail());
        }

        if (patientRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Patient already exists with phone number : " + request.getPhoneNumber());
        }

        Patient patient = new Patient();

        patient.setPatientName(request.getPatientName());
        patient.setGender(request.getGender());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactNumber(request.getEmergencyContactNumber());

        patient = patientRepository.save(patient);

        patient.setPatientCode(generatePatientCode(patient.getId()));

        patient = patientRepository.save(patient);

        return mapToResponse(patient);
    }

    public Page<PatientResponse> getAllPatients(int page,
                                                int size,
                                                String sortBy,
                                                String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        //validate sortBy
        if(!allowedSortFields.contains(sortBy)){
            throw new InvalidOperationException(
                    "Invalid sort Field: " + sortBy
            );
        }

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(sortDirection, sortBy));

        return patientRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    public PatientResponse getPatientById(Long id) {

        Patient patient = patientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found with id : " + id));

        return mapToResponse(patient);
    }

    @Transactional
    public PatientResponse updatePatient(Long id,
                                         UpdatePatientRequest request) {

        Patient patient = patientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found with id : " + id));

        if (patientRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new DuplicateResourceException("Patient already exists with email : " + request.getEmail());
        }

        if (patientRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), id)) {
            throw new DuplicateResourceException("Patient already exists with phone number : " + request.getPhoneNumber());
        }

        patient.setPatientName(request.getPatientName());
        patient.setGender(request.getGender());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyContactNumber(request.getEmergencyContactNumber());

        patient = patientRepository.save(patient);

        return mapToResponse(patient);
    }

    @Transactional
    public String deletePatient(Long id) {

        Patient patient = patientRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found with id : " + id));

        patient.setActive(false);

        patientRepository.save(patient);

        return "Patient deleted successfully.";
    }

    private String generatePatientCode(Long id) {
        return String.format("PAT%03d", id);
    }

    private PatientResponse mapToResponse(Patient patient) {

        PatientResponse response = new PatientResponse();

        response.setId(patient.getId());
        response.setPatientCode(patient.getPatientCode());
        response.setPatientName(patient.getPatientName());
        response.setGender(patient.getGender());
        response.setDateOfBirth(patient.getDateOfBirth());
        response.setBloodGroup(patient.getBloodGroup());
        response.setPhoneNumber(patient.getPhoneNumber());
        response.setEmail(patient.getEmail());
        response.setAddress(patient.getAddress());
        response.setEmergencyContactName(patient.getEmergencyContactName());
        response.setEmergencyContactNumber(patient.getEmergencyContactNumber());
        response.setActive(patient.getActive());
        response.setCreatedAt(patient.getCreatedAt());
        response.setUpdatedAt(patient.getUpdatedAt());

        return response;
    }
}