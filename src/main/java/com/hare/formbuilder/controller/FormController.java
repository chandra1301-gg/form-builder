package com.hare.formbuilder.controller;

import com.hare.formbuilder.dto.request.FormRequest;
import com.hare.formbuilder.dto.response.ApiResponse;
import com.hare.formbuilder.service.FormService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/forms")
@Tag(name = "2. Form Management", description = "CRUD Form endpoints")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    @Operation(summary = "Create Form", description = "Membuat form baru dengan allowed domains dan limit response")
    @PostMapping
    public ResponseEntity<?> createForm(@Valid @RequestBody FormRequest request,
                                         Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthenticated."));
        }

        String email = authentication.getName();
        ApiResponse<?> response = formService.createForm(request, email);

        // Jika validasi error -> 422
        if ("Invalid field".equals(response.getMessage())) {
            return ResponseEntity.status(422).body(response);
        }

        // Jika sukses -> 200 dengan format: { message, form }
        if (response.getData() != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.getData();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "Create form success");
            result.put("form", data.get("form"));
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get All Forms", description = "Mendapatkan daftar semua form")
    @GetMapping
    public ResponseEntity<?> getAllForms(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthenticated."));
        }

        String email = authentication.getName();
        ApiResponse<?> response = formService.getAllForms(email);

        // Kembalikan format: { message, forms }
        if (response.getData() != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.getData();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "Get all forms success");
            result.put("forms", data.get("forms"));
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok(Map.of("message", "Get all forms success", "forms", List.of()));
    }

    @Operation(summary = "Get Form Detail", description = "Melihat detail form termasuk daftar questions")
    @GetMapping("/{formSlug}")
    public ResponseEntity<?> getFormDetail(@PathVariable String formSlug,
                                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthenticated."));
        }

        ApiResponse<?> response = formService.getFormDetail(formSlug);

        // Jika form tidak ditemukan -> 404
        if ("Form not found".equals(response.getMessage())) {
            return ResponseEntity.status(404).body(Map.of("message", "Form not found"));
        }

        // Kembalikan format: { message, form }
        if (response.getData() != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.getData();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "Get form success");
            result.put("form", data.get("form"));
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok(response);
    }
}