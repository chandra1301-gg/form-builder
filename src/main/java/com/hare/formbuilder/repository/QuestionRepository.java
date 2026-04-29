package com.hare.formbuilder.repository;

import com.hare.formbuilder.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByFormId(Long formId);
}