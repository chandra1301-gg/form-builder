package com.hare.formbuilder.repository;

import com.hare.formbuilder.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}