package com.codewithme.smartqueue.repository;

import com.codewithme.smartqueue.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalRepository extends JpaRepository<Hospital,Long> {


    Page<Hospital> findByActiveTrue(Pageable pageable);

    Page<Hospital> findByActiveTrueAndCity(
            String city,
            Pageable pageable);
}
