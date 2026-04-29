package com.hare.formbuilder.controller;

import com.hare.formbuilder.dto.request.SubmitResponseRequest;
import com.hare.formbuilder.dto.response.ApiResponse;
import com.hare.formbuilder.service.ResponseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/forms/{formSlug}/responses")
@Tag(name = "4. Response Management", description = "Submit & View Response endpoints")
public class ResponseController {

    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @Operation(summary = "Submit Response", 
               description = "Mengirim respons form. Terdapat validasi domain email dan limit 1 response")
    @PostMapping
    public ResponseEntity<?> submitResponse(@PathVariable String formSlug,
                                            @RequestBody SubmitResponseRequest request,
                                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthenticated."));
        }

        String email = authentication.getName();
        ApiResponse<?> response = responseService.submitResponse(formSlug, request, email);

        if ("Form not found".equals(response.getMessage())) {
            return ResponseEntity.status(404).body(Map.of("message", "Form not found"));
        }

        if ("Forbidden access".equals(response.getMessage())) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden access"));
        }

        if ("You can not submit form twice".equals(response.getMessage())) {
            return ResponseEntity.status(422).body(Map.of("message", "You can not submit form twice"));
        }

        return ResponseEntity.ok(Map.of("message", "Submit response success"));
    }

    @Operation(summary = "Get All Responses", 
               description = "Melihat semua respons form. Hanya dapat diakses oleh creator form")
    @GetMapping
    public ResponseEntity<?> getAllResponses(@PathVariable String formSlug,
                                             Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthenticated."));
        }

        String email = authentication.getName();
        ApiResponse<?> response = responseService.getAllResponses(formSlug, email);

        if ("Form not found".equals(response.getMessage())) {
            return ResponseEntity.status(404).body(Map.of("message", "Form not found"));
        }

        if ("Forbidden access".equals(response.getMessage())) {
            return ResponseEntity.status(403).body(Map.of("message", "Forbidden access"));
        }

        // Sukses -> 200: { message, responses }
        if (response.getData() != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.getData();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "Get responses success");
            result.put("responses", data.get("responses"));
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok(Map.of("message", "Get responses success", "responses", List.of()));
    }
}