package com.hare.formbuilder.repository;

import com.hare.formbuilder.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long> {
    Optional<Form> findBySlug(String slug);
    List<Form> findByCreatorId(Long creatorId);
    boolean existsBySlug(String slug);
}