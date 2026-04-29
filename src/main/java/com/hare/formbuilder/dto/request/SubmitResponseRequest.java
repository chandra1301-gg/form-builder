package com.hare.formbuilder.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SubmitResponseRequest {

    @NotNull(message = "The answers field is required.")
    private List<AnswerValue> answers;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class AnswerValue {

        @NotNull(message = "The question id field is required.")
        private Long questionId;

        private Object value;
    }
}