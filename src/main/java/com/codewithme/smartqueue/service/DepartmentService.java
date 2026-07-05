package com.codewithme.smartqueue.service;

import com.codewithme.smartqueue.dto.request.CreateDepartmentRequest;
import com.codewithme.smartqueue.dto.request.UpdateDepartmentRequest;
import com.codewithme.smartqueue.dto.response.DepartmentResponse;
import com.codewithme.smartqueue.dto.response.HospitalResponse;
import com.codewithme.smartqueue.entity.Department;
import com.codewithme.smartqueue.entity.Hospital;
import com.codewithme.smartqueue.exception.DuplicateResourceException;
import com.codewithme.smartqueue.exception.InvalidOperationException;
import com.codewithme.smartqueue.exception.ResourceNotFoundException;
import com.codewithme.smartqueue.repository.DepartmentRepository;
import com.codewithme.smartqueue.repository.HospitalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;




@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;

    //constructor injection
    public  DepartmentService(DepartmentRepository departmentRepository,
                              HospitalRepository hospitalRepository){
        this.departmentRepository = departmentRepository;
        this.hospitalRepository = hospitalRepository;
    }

    private final List<String> allowedSortFields = List.of(
            "id",
            "departmentName",
            "createdAt"
    );


    private DepartmentResponse mapToDepartmentResponse(Department department) {
        DepartmentResponse response = new DepartmentResponse();

        response.setId(department.getId());
        response.setDepartmentName(department.getDepartmentName());
        response.setDescription(department.getDescription());

        response.setHospitalId(department.getHospital().getId());
        response.setHospitalName(department.getHospital().getHospitalName());

        response.setActive(department.isActive());
        response.setCreatedAt(department.getCreatedAt());
        response.setUpdatedAt(department.getUpdatedAt());

        return response;
    }
    public DepartmentResponse createDepartment(CreateDepartmentRequest request){
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(()->
                        new ResourceNotFoundException("Hospital not found"));


        //object creation
        Department department = Department.builder()
                .departmentName(request.getDepartmentName())
                .description(request.getDescription())
                .hospital(hospital)
                .build();

        if (departmentRepository.existsByHospitalAndDepartmentName(
                hospital,
                request.getDepartmentName())) {

            throw new DuplicateResourceException(
                    "Department already exists in this hospital");
        }
        //saving in DB
        Department savedDepartment = departmentRepository.save(department);

        return mapToDepartmentResponse(savedDepartment);
    }

    //get all departments
    public Page<DepartmentResponse> getAllDepartments(
            int page,
            int size,
            String sortBy,
            String direction
        ){

        //validate direction
        Sort.Direction sortDirection;

        try{
            sortDirection = Sort.Direction.valueOf(direction.toUpperCase());
        }
        catch (IllegalArgumentException ex){
            throw new InvalidOperationException(
                    "Sort Direction must be ASC or DESC"
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
        Page<Department> departments =
                departmentRepository.findByActiveTrue(pageable);



        //map Entity->DTO
        return departments.map(this::mapToDepartmentResponse);
    }

    //get department by id
    public DepartmentResponse getDepartmentById(Long id) {

        Department department = departmentRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found"));

        return mapToDepartmentResponse(department);
    }

    //update department from one hospital to another
    public DepartmentResponse updateDepartment(
            Long id,
            UpdateDepartmentRequest request){

        // Retrieve the existing active department
        Department department = departmentRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found"));


        //finding the target hospital
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Hospital not found"));

        //checking duplicate departments
        if (departmentRepository
                .existsByHospitalAndDepartmentNameAndIdNot(
                        hospital,
                        request.getDepartmentName(),
                        id)) {

            throw new DuplicateResourceException(
                    "Department already exists in this hospital");
        }

        //updating
        department.setDepartmentName(request.getDepartmentName());
        department.setDescription(request.getDescription());
        department.setHospital(hospital);

        //repository call
        Department updatedDepartment =
                departmentRepository.save(department);

        return mapToDepartmentResponse(updatedDepartment);
    }

    //soft deleting
    public String deleteDepartment(Long id){
        Department department = departmentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Department Not Found with id :" + id));

        if(!department.isActive()){
            throw new InvalidOperationException("Department is Already inactive");
        }

        department.setActive(false);
        departmentRepository.save(department);
        return "Department deleted successfully";
    }
}
