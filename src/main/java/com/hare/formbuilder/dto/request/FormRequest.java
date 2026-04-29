package com.hare.formbuilder.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FormRequest {

    @NotBlank(message = "The name field is required.")
    private String name;

    @NotBlank(message = "The slug field is required.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-\\.]+$",
             message = "The slug must be alphanumeric with dash or dot only.")
    private String slug;

    private String description;

    private List<String> allowedDomains;

    private Boolean limitOneResponse;
}