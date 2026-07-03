package com.codewithme.smartqueue.repository;

import com.codewithme.smartqueue.entity.Department;
import com.codewithme.smartqueue.entity.Hospital;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository  extends JpaRepository<Department,Long> {

    boolean existsByHospitalAndDepartmentName(
            Hospital hospital,
            String departmentName
    );

    boolean existsByHospitalAndDepartmentNameAndIdNot(
            Hospital hospital,
            String departmentName,
            Long id
    );

    Page<Department> findByActiveTrue(Pageable pageable);


    Optional<Department> findByIdAndActiveTrue(Long id);

  //  Optional<Department> findByIdAndActiveTrue(Long id);
}
