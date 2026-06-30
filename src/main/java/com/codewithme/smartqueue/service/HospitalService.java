package com.codewithme.smartqueue.service;

import com.codewithme.smartqueue.dto.request.CreateHospitalRequest;
import com.codewithme.smartqueue.dto.request.UpdateHospitalRequest;
import com.codewithme.smartqueue.dto.response.HospitalResponse;
import com.codewithme.smartqueue.entity.Hospital;
import com.codewithme.smartqueue.enums.HospitalStatus;
import com.codewithme.smartqueue.exception.InvalidOperationException;
import com.codewithme.smartqueue.exception.ResourceNotFoundException;
import com.codewithme.smartqueue.repository.HospitalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    private static final List<String> allowedSortFields = List.of(
            "hospitalName",
            "hospitalCode",
            "city",
            "state",
            "createdAt"
    );

    public HospitalService(HospitalRepository hospitalRepository){
        this.hospitalRepository=hospitalRepository;
    }

    public HospitalResponse createHospital(CreateHospitalRequest request){
        // Request DTO -> Entity
        Hospital hospital = new Hospital();

        hospital.setHospitalName(request.getHospitalName());
        hospital.setHospitalCode(request.getHospitalCode());
        hospital.setAddress(request.getAddress());
        hospital.setCity(request.getCity());
        hospital.setState(request.getState());
        hospital.setPhoneNumber(request.getPhoneNumber());
        hospital.setEmail(request.getEmail());
        hospital.setStatus(HospitalStatus.PENDING_APPROVAL);

        // Save Entity
        Hospital savedHospital = hospitalRepository.save(hospital);

        // Entity -> Response DTO
        return mapToHospitalResponse(savedHospital);
    }

    private HospitalResponse mapToHospitalResponse(Hospital hospital) {

        HospitalResponse response = new HospitalResponse();

        response.setId(hospital.getId());
        response.setHospitalName(hospital.getHospitalName());
        response.setHospitalCode(hospital.getHospitalCode());
        response.setAddress(hospital.getAddress());
        response.setCity(hospital.getCity());
        response.setState(hospital.getState());
        response.setPhoneNumber(hospital.getPhoneNumber());
        response.setEmail(hospital.getEmail());
        response.setStatus(hospital.getStatus());
        response.setCreatedAt(hospital.getCreatedAt());
        response.setUpdatedAt(hospital.getUpdatedAt());

        return response;
    }

  public Page<HospitalResponse> getAllHospitals(
          int page,
          int size,
          String sortBy,
          String direction,
          String city){

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

        //validation of city
        if(city != null && city.isBlank()){
            throw new InvalidOperationException(
                    "city cannot be black"
            );
        }

        //repository call
        Page<Hospital> hospitals;

        if(city == null){
            hospitals = hospitalRepository.findByActiveTrue(pageable);

        }
        else{
            hospitals = hospitalRepository.findByActiveTrueAndCity(city , pageable);
        }

        //map Entity->DTO
        return hospitals.map(this::mapToHospitalResponse);
    }

    //get by id
    public HospitalResponse getHospitalById(Long id){
        Hospital hospital=hospitalRepository.findById(id)
                .orElseThrow(()->
                        new ResourceNotFoundException("Hospital not found with id :" + id));

        return mapToHospitalResponse(hospital);
    }

    //updating
    public HospitalResponse updateHospital(Long id, UpdateHospitalRequest request) {

        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id : " + id));

        hospital.setHospitalName(request.getHospitalName());
        hospital.setAddress(request.getAddress());
        hospital.setCity(request.getCity());
        hospital.setState(request.getState());
        hospital.setPhoneNumber(request.getPhoneNumber());
        hospital.setEmail(request.getEmail());

        hospitalRepository.save(hospital);

        return mapToHospitalResponse(hospital);
    }
    //Soft deleting , means making inactive
    public String deleteHospital(Long id){
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hospital Not Found with id :" + id));

        if(!hospital.isActive()){
            throw new InvalidOperationException("Hospital is Already inactive");
        }

        hospital.setActive(false);
        hospitalRepository.save(hospital);
        return "Hospital deleted successfully";
    }
}
