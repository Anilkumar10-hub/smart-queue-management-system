package com.codewithme.smartqueue.repository;

import com.codewithme.smartqueue.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmailAndIdNot(String email, Long id);


    Optional<Doctor> findByIdAndActiveTrue(Long id);

    Page<Doctor> findByActiveTrue(Pageable pageable);

}