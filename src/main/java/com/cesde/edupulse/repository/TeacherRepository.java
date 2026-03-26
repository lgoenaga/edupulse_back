package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.Teacher;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    List<Teacher> findAllByOrderByFirstNameAscLastNameAsc();

    Page<Teacher> findAllByOrderByFirstNameAscLastNameAsc(Pageable pageable);

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByDocumentNumberAndIdNot(String documentNumber, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}