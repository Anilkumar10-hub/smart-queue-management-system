package com.codewithme.smartqueue.service;

import com.codewithme.smartqueue.dto.request.CreateHospitalRequest;
import com.codewithme.smartqueue.dto.request.UpdateHospitalRequest;
import com.codewithme.smartqueue.dto.response.HospitalResponse;
import com.codewithme.smartqueue.entity.Hospital;
import com.codewithme.smartqueue.enums.HospitalStatus;
import com.codewithme.smartqueue.exception.InvalidOperationException;
import com.codewithme.smartqueue.exception.ResourceNotFoundException;
import com.codewithme.smartqueue.repository.HospitalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;

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
  public List<HospitalResponse> getAllHospitals(){

        List<Hospital> hospitals = hospitalRepository.findByActiveTrue();

        List<HospitalResponse> responses = new ArrayList<>();

        for(Hospital hospital:hospitals){
            HospitalResponse response = mapToHospitalResponse(hospital);
            responses.add(response);
        }

        return responses;
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
            throw new InvalidOperationException("Hospital is Already incative");
        }

        hospital.setActive(false);
        hospitalRepository.save(hospital);
        return "Hospital deleted successfully";
    }
}
