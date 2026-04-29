package com.hare.formbuilder.service;

import com.hare.formbuilder.dto.request.QuestionRequest;
import com.hare.formbuilder.dto.response.ApiResponse;
import com.hare.formbuilder.entity.*;
import com.hare.formbuilder.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionService {

    private final FormRepository formRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionService(FormRepository formRepository,
                           QuestionRepository questionRepository,
                           UserRepository userRepository) {
        this.formRepository = formRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    public ApiResponse<?> addQuestion(String formSlug, QuestionRequest request, String email) {
        Optional<Form> formOpt = formRepository.findBySlug(formSlug);
        if (formOpt.isEmpty()) {
            return ApiResponse.builder().message("Form not found").build();
        }

        Form form = formOpt.get();

        // ⚠️ CEK KEPEMILIKAN FORM
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null || !currentUser.getId().equals(form.getCreatorId())) {
            return ApiResponse.builder().message("Forbidden access").build();
        }

        // Validasi choice type
        List<String> validTypes = List.of("short answer", "paragraph", "date",
                "multiple choice", "dropdown", "checkboxes");
        if (!validTypes.contains(request.getChoiceType().toLowerCase())) {
            Map<String, List<String>> errors = new HashMap<>();
            errors.put("choice_type", List.of("The choice type must be one of: " + String.join(", ", validTypes)));
            return ApiResponse.builder().message("Invalid field").errors(errors).build();
        }

        // Validasi choices untuk tipe yang memerlukan
        List<String> requiresChoices = List.of("multiple choice", "dropdown", "checkboxes");
        if (requiresChoices.contains(request.getChoiceType().toLowerCase()) &&
                (request.getChoices() == null || request.getChoices().isEmpty())) {
            Map<String, List<String>> errors = new HashMap<>();
            errors.put("choices", List.of("The choices field is required for this choice type."));
            return ApiResponse.builder().message("Invalid field").errors(errors).build();
        }

        // Buat question
        Question question = Question.builder()
                .name(request.getName())
                .choiceType(request.getChoiceType().toLowerCase())
                .choices(request.getChoices() != null ? String.join(",", request.getChoices()) : null)
                .isRequired(request.getIsRequired() != null ? request.getIsRequired() : false)
                .form(form)
                .build();

        question = questionRepository.save(question);

        // Response
        Map<String, Object> questionData = new LinkedHashMap<>();
        questionData.put("name", question.getName());
        questionData.put("choice_type", question.getChoiceType());
        questionData.put("is_required", question.getIsRequired());
        questionData.put("choices", question.getChoices());
        questionData.put("form_id", form.getId());
        questionData.put("id", question.getId());

        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("question", questionData);

        return ApiResponse.builder()
                .message("Add question success")
                .data(responseData)
                .build();
    }

    public ApiResponse<?> removeQuestion(String formSlug, Long questionId, String email) {
        Optional<Form> formOpt = formRepository.findBySlug(formSlug);
        if (formOpt.isEmpty()) {
            return ApiResponse.builder().message("Form not found").build();
        }

        Form form = formOpt.get();

        // ⚠️ CEK KEPEMILIKAN FORM
        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser == null || !currentUser.getId().equals(form.getCreatorId())) {
            return ApiResponse.builder().message("Forbidden access").build();
        }

        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isEmpty()) {
            return ApiResponse.builder().message("Question not found").build();
        }

        Question question = questionOpt.get();
        if (!question.getForm().getId().equals(form.getId())) {
            return ApiResponse.builder().message("Question not found").build();
        }

        questionRepository.delete(question);

        return ApiResponse.builder().message("Remove question success").build();
    }
}