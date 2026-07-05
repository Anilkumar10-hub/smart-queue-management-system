package com.codewithme.smartqueue.service;

import com.codewithme.smartqueue.dto.request.CreateDepartmentRequest;
import com.codewithme.smartqueue.dto.request.CreateDoctorRequest;
import com.codewithme.smartqueue.dto.request.UpdateDoctorRequest;
import com.codewithme.smartqueue.dto.response.DoctorResponse;
import com.codewithme.smartqueue.entity.Department;
import com.codewithme.smartqueue.entity.Doctor;
import com.codewithme.smartqueue.exception.DuplicateResourceException;
import com.codewithme.smartqueue.exception.InvalidOperationException;
import com.codewithme.smartqueue.exception.ResourceNotFoundException;
import com.codewithme.smartqueue.repository.DepartmentRepository;
import com.codewithme.smartqueue.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    public DoctorService(DoctorRepository doctorRepository, DepartmentRepository departmentRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
    }

    private final List<String> allowedSortFields = List.of(
            "id",
            "doctorName",
            "createdAt"
    );

    //a helper fun for getting Doctor code
    private String generateDoctorCode(Long id){
        return String.format("DC%03d",id);
    }

    @Transactional
    private DoctorResponse mapToDoctorResponse(Doctor doctor){
        DoctorResponse response = new DoctorResponse();

        response.setId(doctor.getId());

        response.setDoctorCode(doctor.getDoctorCode());

        response.setDoctorName(doctor.getDoctorName());

        response.setEmail(doctor.getEmail());

        response.setPhoneNumber(doctor.getPhoneNumber());

        response.setQualification(doctor.getQualification());

        response.setYearsOfExperience(doctor.getYearsOfExperience());

        response.setJoiningDate(doctor.getJoiningDate());

        response.setConsultationFee(doctor.getConsultationFee());
        response.setAvailabilityStatus(doctor.getAvailabilityStatus());
        response.setActive(doctor.isActive());
        response.setCreatedAt(doctor.getCreatedAt());
        response.setUpdatedAt(doctor.getUpdatedAt());

        // Department Mapping
        response.setDepartmentId(doctor.getDepartment().getId());
        response.setDepartmentName(doctor.getDepartment().getDepartmentName());

        return response;

    }

    public DoctorResponse createDoctor(CreateDoctorRequest request){
        Department department = departmentRepository
                .findByIdAndActiveTrue(request.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department not found with id : " + request.getDepartmentId()
                        )
                );




        //for unique Email
        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Doctor already exists with email : " + request.getEmail()
            );
        }

        //unique phone number
        if (doctorRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException(
                    "Doctor already exists with phone number : " + request.getPhoneNumber()
            );
        }

        Doctor doctor = Doctor.builder()
                .doctorName(request.getDoctorName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .qualification(request.getQualification())
                .specialization(request.getSpecialization())
                .yearsOfExperience(request.getYearsOfExperience())
                .joiningDate(request.getJoiningDate())
                .consultationFee(request.getConsultationFee())
                .department(department)
                .build();

            //before saving the ID is NULL
            doctor = doctorRepository.save(doctor);

            //after saving ID is generated
            //so now we get it
            doctor.setDoctorCode(generateDoctorCode(doctor.getId()));
            doctor = doctorRepository.save(doctor);

            return mapToDoctorResponse(doctor);

    }

    public Page<DoctorResponse> getAllDoctors(
            int page,
            int size,
            String sortBy,
            String direction
    ){
        //validate direction
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.valueOf(direction.toUpperCase());
        }
        catch (IllegalArgumentException ex){
            throw  new InvalidOperationException(
                    "Sort direction must be ASC or DESC"
            );
        }

        //validate sortBy
        if(!allowedSortFields.contains(sortBy)){
            throw new InvalidOperationException(
                    "Invalid sort Field: " + sortBy
            );
        }

        //creating pageable
        Pageable pageable= PageRequest.of(
                page,
                size,
                Sort.by(sortDirection,sortBy));

        //repository call
        Page<Doctor> doctors =
                doctorRepository.findByActiveTrue(pageable);

        //map Entity->DTO
        return doctors.map(this::mapToDoctorResponse);

    }

    //update
    @Transactional
    public DoctorResponse updateDoctor(
            long id,
            UpdateDoctorRequest request) {

        //retriving existing active doctor
        Doctor doctor = doctorRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Doctor not Found"));

        //finding target Department
        Department department = departmentRepository.findByIdAndActiveTrue(request.getDepartmentId())
                .orElseThrow(()-> new ResourceNotFoundException("Doctor Not Found"));


        //check for duplicate doctors
        if(doctorRepository
                .existsByEmailAndIdNot(
                        request.getEmail(),
                        id)){

            throw new DuplicateResourceException(
                    "Doctor Already exist with same email");
        }

        //updating
        doctor.setDoctorName(request.getDoctorName());
        doctor.setEmail(request.getEmail());
        doctor.setPhoneNumber(request.getPhoneNumber());
        doctor.setQualification(request.getQualification());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setYearsOfExperience(request.getYearsOfExperience());
        doctor.setJoiningDate(request.getJoiningDate());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setDepartment(department);

        //repository call
        Doctor updateDoctor =
                doctorRepository.save(doctor);

        return mapToDoctorResponse(updateDoctor);
    }

    //get doctor by id
    public DoctorResponse getDoctorById(Long id){
        Doctor doctor = doctorRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Doctor Not Found"));

        return mapToDoctorResponse(doctor);
    }

    //soft delete
    public String deleteDoctorById(Long id){
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Doctor not Found with Id " + id));

        if(!doctor.isActive()){
            throw new InvalidOperationException("Doctor is Already inactive");
        }

        doctor.setActive(false);
        doctorRepository.save(doctor);
        return "Doctor deleted successfully";
    }
}
