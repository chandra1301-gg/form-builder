package com.hare.formbuilder.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginRequest {

    @NotBlank(message = "The email field is required.")
    @Email(message = "The email must be a valid email address.")
    private String email;

    @NotBlank(message = "The password field is required.")
    @Size(min = 5, message = "The password must be at least 5 characters.")
    private String password;
}