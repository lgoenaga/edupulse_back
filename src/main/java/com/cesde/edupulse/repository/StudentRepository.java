package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAllByOrderByFirstNameAscLastNameAsc();

    Optional<Student> findByUserUsername(String username);

    boolean existsByGroupId(Long groupId);

    boolean existsByStudentCode(String studentCode);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByStudentCodeAndIdNot(String studentCode, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}