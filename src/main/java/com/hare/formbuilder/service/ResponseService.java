package com.hare.formbuilder.service;

import com.hare.formbuilder.dto.request.SubmitResponseRequest;
import com.hare.formbuilder.dto.response.ApiResponse;
import com.hare.formbuilder.entity.*;
import com.hare.formbuilder.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ResponseService {

    private final FormRepository formRepository;
    private final UserRepository userRepository;
    private final ResponseRepository responseRepository;
    private final QuestionRepository questionRepository;

    public ResponseService(FormRepository formRepository, UserRepository userRepository,
                           ResponseRepository responseRepository, QuestionRepository questionRepository) {
        this.formRepository = formRepository;
        this.userRepository = userRepository;
        this.responseRepository = responseRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public ApiResponse<?> submitResponse(String formSlug, SubmitResponseRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Form> formOpt = formRepository.findBySlug(formSlug);
        if (formOpt.isEmpty()) {
            return ApiResponse.builder().message("Form not found").build();
        }

        Form form = formOpt.get();

        // Cek domain
        if (form.getAllowedDomains() != null && !form.getAllowedDomains().isEmpty()) {
            String userDomain = email.substring(email.indexOf("@") + 1);
            boolean domainAllowed = form.getAllowedDomains().stream()
                    .anyMatch(ad -> ad.getDomain().equalsIgnoreCase(userDomain));
            if (!domainAllowed) {
                return ApiResponse.builder().message("Forbidden access").build();
            }
        }

        // Cek limit one response
        if (Boolean.TRUE.equals(form.getLimitOneResponse())) {
            Optional<Response> existingResp = responseRepository.findByFormIdAndUserId(form.getId(), user.getId());
            if (existingResp.isPresent()) {
                return ApiResponse.builder().message("You can not submit form twice").build();
            }
        }

        // Buat response
        Response response = Response.builder()
                .formId(form.getId())
                .userId(user.getId())
                .date(LocalDateTime.now())
                .build();

        for (SubmitResponseRequest.AnswerValue av : request.getAnswers()) {
            String valueStr;
            if (av.getValue() instanceof String) {
                valueStr = (String) av.getValue();
            } else if (av.getValue() instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) av.getValue();
                valueStr = String.join(",", values);
            } else {
                valueStr = av.getValue() != null ? av.getValue().toString() : "";
            }

            Answer answer = Answer.builder()
                    .questionId(av.getQuestionId())
                    .value(valueStr)
                    .response(response)
                    .build();
            response.getAnswers().add(answer);
        }

        responseRepository.save(response);

        return ApiResponse.builder().message("Submit response success").build();
    }

    public ApiResponse<?> getAllResponses(String formSlug, String email) {
        Optional<Form> formOpt = formRepository.findBySlug(formSlug);
        if (formOpt.isEmpty()) {
            return ApiResponse.builder().message("Form not found").build();
        }

        Form form = formOpt.get();
        User currentUser = userRepository.findByEmail(email).orElse(null);

        // Hanya creator yang bisa lihat
        if (currentUser == null || !currentUser.getId().equals(form.getCreatorId())) {
            return ApiResponse.builder().message("Forbidden access").build();
        }

        List<Response> responses = responseRepository.findByFormId(form.getId());
        List<Question> questions = questionRepository.findByFormId(form.getId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Response resp : responses) {
            Map<String, Object> respData = new LinkedHashMap<>();
            respData.put("date", resp.getDate() != null ? resp.getDate().format(formatter) : null);

            // Data user yang submit
            User respUser = userRepository.findById(resp.getUserId()).orElse(null);
            Map<String, Object> userData = new LinkedHashMap<>();
            if (respUser != null) {
                userData.put("id", respUser.getId());
                userData.put("name", respUser.getName());
                userData.put("email", respUser.getEmail());
                userData.put("email_verified_at", null);
            }
            respData.put("user", userData);

            // Answers dengan key = nama question
            Map<String, String> answers = new LinkedHashMap<>();
            for (Answer a : resp.getAnswers()) {
                Question q = questions.stream()
                        .filter(x -> x.getId().equals(a.getQuestionId()))
                        .findFirst().orElse(null);
                String questionName = q != null ? q.getName() : "Question " + a.getQuestionId();
                answers.put(questionName, a.getValue());
            }
            respData.put("answers", answers);

            responseList.add(respData);
        }

        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("responses", responseList);

        return ApiResponse.builder()
                .message("Get responses success")
                .data(responseData)
                .build();
    }
}