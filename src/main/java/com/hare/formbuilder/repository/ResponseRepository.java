package com.hare.formbuilder.repository;

import com.hare.formbuilder.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByFormId(Long formId);
    Optional<Response> findByFormIdAndUserId(Long formId, Long userId);
}