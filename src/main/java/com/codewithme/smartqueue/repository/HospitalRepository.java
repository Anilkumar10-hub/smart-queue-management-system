package com.codewithme.smartqueue.repository;

import com.codewithme.smartqueue.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HospitalRepository extends JpaRepository<Hospital,Long> {
    List<Hospital> findByActiveTrue();
}
