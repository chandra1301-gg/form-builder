package com.hare.formbuilder.controller;

import com.hare.formbuilder.dto.request.QuestionRequest;
import com.hare.formbuilder.dto.response.ApiResponse;
import com.hare.formbuilder.service.QuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/forms/{formSlug}/questions")
@Tag(name = "3. Question Management", description = "Add & Remove Question endpoints")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Operation(summary = "Add Question", 
               description = "Menambahkan pertanyaan ke form. Tipe: short answer, paragraph, date, multiple choice, dropdown, checkboxes")
    @PostMapping
    public ResponseEntity<?> addQuestion(@PathVariable String formSlug,
                                         @Valid @RequestBody QuestionRequest request,
                                         Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthenticated."));
        }

        String email = authentication.getName();
        ApiResponse<?> response = questionService.addQuestion(formSlug, request, email);

        // Form not found -> 404
        if ("Form not found".equals(response.getMessage())) {
            return ResponseEntity.status(404).body(Map.of("message", "Form not found"));
        }

        // Forbidden access -> 403
        if ("Forbidden access".equals(response.getMessage())) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden access"));
        }

        // Invalid field -> 422
        if ("Invalid field".equals(response.getMessage())) {
            return ResponseEntity.status(422).body(response);
        }

        // Sukses -> 200
        if (response.getData() != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.getData();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "Add question success");
            result.put("question", data.get("question"));
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove Question", description = "Menghapus pertanyaan dari form")
    @DeleteMapping("/{questionId}")
    public ResponseEntity<?> removeQuestion(@PathVariable String formSlug,
                                            @PathVariable Long questionId,
                                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthenticated."));
        }

        String email = authentication.getName();
        ApiResponse<?> response = questionService.removeQuestion(formSlug, questionId, email);

        // Form not found -> 404
        if ("Form not found".equals(response.getMessage())) {
            return ResponseEntity.status(404).body(Map.of("message", "Form not found"));
        }

        // Forbidden access -> 403
        if ("Forbidden access".equals(response.getMessage())) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden access"));
        }

        // Question not found -> 404
        if ("Question not found".equals(response.getMessage())) {
            return ResponseEntity.status(404).body(Map.of("message", "Question not found"));
        }

        // Sukses -> 200
        return ResponseEntity.ok(Map.of("message", "Remove question success"));
    }
}