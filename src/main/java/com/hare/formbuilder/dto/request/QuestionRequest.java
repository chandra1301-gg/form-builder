package com.hare.formbuilder.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QuestionRequest {

    @NotBlank(message = "The name field is required.")
    private String name;

    @NotBlank(message = "The choice type field is required.")
    private String choiceType;

    private List<String> choices;

    private Boolean isRequired;
}