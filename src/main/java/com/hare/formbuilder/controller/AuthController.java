package com.hare.formbuilder.controller;

import com.hare.formbuilder.dto.request.LoginRequest;
import com.hare.formbuilder.dto.response.ApiResponse;
import com.hare.formbuilder.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "1. Authentication", description = "Login & Logout endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User Login", description = "Login dengan email dan password untuk mendapatkan access token JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        ApiResponse<?> response = authService.login(request);

        // Jika email atau password salah -> 401
        if ("Email or password incorrect".equals(response.getMessage())) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Email or password incorrect"));
        }

        // Jika validasi error -> 422
        if ("Invalid field".equals(response.getMessage())) {
            return ResponseEntity.status(422).body(response);
        }

        // Jika sukses -> 200 dengan format: { message, user }
        if ("Login success".equals(response.getMessage()) && response.getData() != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.getData();
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("message", "Login success");
            result.put("user", data.get("user"));
            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User Logout", description = "Logout dari sistem (stateless)")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Unauthenticated."));
        }
        return ResponseEntity.ok(Map.of("message", "Logout success"));
    }
}