package com.hare.formbuilder.service;

import com.hare.formbuilder.dto.request.LoginRequest;
import com.hare.formbuilder.dto.response.ApiResponse;
import com.hare.formbuilder.entity.User;
import com.hare.formbuilder.repository.UserRepository;
import com.hare.formbuilder.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ApiResponse<?> login(LoginRequest request) {
        // Validasi user
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ApiResponse.builder()
                    .message("Email or password incorrect")
                    .build();
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponse.builder()
                    .message("Email or password incorrect")
                    .build();
        }

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getEmail());

        // Buat user data sesuai format dokumen
        Map<String, Object> userData = new LinkedHashMap<>();
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("accessToken", token);

        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("user", userData);

        return ApiResponse.builder()
                .message("Login success")
                .data(responseData)
                .build();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}