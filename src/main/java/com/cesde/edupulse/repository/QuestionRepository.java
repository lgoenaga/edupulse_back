package com.cesde.edupulse.repository;

import com.cesde.edupulse.domain.model.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByActiveTrueOrderByDisplayOrderAsc();
}