package com.hare.formbuilder.service;

import com.hare.formbuilder.dto.request.FormRequest;
import com.hare.formbuilder.dto.response.ApiResponse;
import com.hare.formbuilder.entity.*;
import com.hare.formbuilder.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FormService {

    private final FormRepository formRepository;
    private final UserRepository userRepository;

    public FormService(FormRepository formRepository, UserRepository userRepository) {
        this.formRepository = formRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ApiResponse<?> createForm(FormRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (formRepository.existsBySlug(request.getSlug())) {
            Map<String, List<String>> errors = new HashMap<>();
            errors.put("slug", List.of("The slug has already been taken."));
            return ApiResponse.builder()
                    .message("Invalid field")
                    .errors(errors)
                    .build();
        }

        // ===== DEBUG =====
        System.out.println("==========================================");
        System.out.println("DEBUG CREATE FORM:");
        System.out.println("  Request class: " + request.getClass().getName());
        System.out.println("  getLimitOneResponse(): " + request.getLimitOneResponse());
        System.out.println("  getLimitOneResponse() class: " + 
            (request.getLimitOneResponse() != null ? request.getLimitOneResponse().getClass().getName() : "NULL"));
        System.out.println("==========================================");

        // BUAT OBJECT FORM DENGAN SETTER (BUKAN BUILDER)
        Form form = new Form();
        form.setName(request.getName());
        form.setSlug(request.getSlug());
        form.setDescription(request.getDescription());
        form.setCreatorId(user.getId());
        
        // SET LIMIT ONE RESPONSE
        Boolean limitOne = request.getLimitOneResponse();
        System.out.println("  Sebelum set: limitOne = " + limitOne);
        
        form.setLimitOneResponse(limitOne);
        System.out.println("  Setelah setLimitOneResponse: " + form.getLimitOneResponse());
        
        // Set allowed domains
        if (request.getAllowedDomains() != null) {
            for (String domain : request.getAllowedDomains()) {
                AllowedDomain ad = new AllowedDomain();
                ad.setDomain(domain.trim());
                ad.setForm(form);
                form.getAllowedDomains().add(ad);
            }
        }

        // SAVE
        Form saved = formRepository.save(form);
        System.out.println("  Saved to DB, limitOneResponse: " + saved.getLimitOneResponse());
        
        // AMBIL ULANG DARI DATABASE
        Form reCheck = formRepository.findById(saved.getId()).orElse(null);
        if (reCheck != null) {
            System.out.println("  Re-check from DB, limitOneResponse: " + reCheck.getLimitOneResponse());
        }
        System.out.println("==========================================");

        // Response
        Map<String, Object> formData = new LinkedHashMap<>();
        formData.put("name", saved.getName());
        formData.put("slug", saved.getSlug());
        formData.put("description", saved.getDescription());
        formData.put("limit_one_response", saved.getLimitOneResponse());
        formData.put("creator_id", saved.getCreatorId());
        formData.put("id", saved.getId());

        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("form", formData);

        return ApiResponse.builder()
                .message("Create form success")
                .data(responseData)
                .build();
    }

    public ApiResponse<?> getAllForms(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Form> forms = formRepository.findByCreatorId(user.getId());

        List<Map<String, Object>> formList = new ArrayList<>();
        for (Form form : forms) {
            Map<String, Object> f = new LinkedHashMap<>();
            f.put("id", form.getId());
            f.put("name", form.getName());
            f.put("slug", form.getSlug());
            f.put("description", form.getDescription());
            f.put("limit_one_response", Boolean.TRUE.equals(form.getLimitOneResponse()) ? 1 : 0);
            f.put("creator_id", form.getCreatorId());

            List<String> domains = new ArrayList<>();
            if (form.getAllowedDomains() != null) {
                for (AllowedDomain ad : form.getAllowedDomains()) {
                    domains.add(ad.getDomain());
                }
            }
            f.put("allowed_domains", domains);

            formList.add(f);
        }

        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("forms", formList);

        return ApiResponse.builder()
                .message("Get all forms success")
                .data(responseData)
                .build();
    }

    public ApiResponse<?> getFormDetail(String slug) {
        Optional<Form> formOpt = formRepository.findBySlug(slug);
        if (formOpt.isEmpty()) {
            return ApiResponse.builder()
                    .message("Form not found")
                    .build();
        }

        Form form = formOpt.get();

        Map<String, Object> formData = new LinkedHashMap<>();
        formData.put("id", form.getId());
        formData.put("name", form.getName());
        formData.put("slug", form.getSlug());
        formData.put("description", form.getDescription());
        formData.put("limit_one_response", Boolean.TRUE.equals(form.getLimitOneResponse()) ? 1 : 0);
        formData.put("creator_id", form.getCreatorId());

        List<String> domains = new ArrayList<>();
        if (form.getAllowedDomains() != null) {
            for (AllowedDomain ad : form.getAllowedDomains()) {
                domains.add(ad.getDomain());
            }
        }
        formData.put("allowed_domains", domains);

        List<Map<String, Object>> questions = new ArrayList<>();
        if (form.getQuestions() != null) {
            for (Question q : form.getQuestions()) {
                Map<String, Object> qData = new LinkedHashMap<>();
                qData.put("id", q.getId());
                qData.put("name", q.getName());
                qData.put("choice_type", q.getChoiceType());
                qData.put("choices", q.getChoices() != null ? q.getChoices().split(",") : new String[]{});
                qData.put("is_required", q.getIsRequired());
                questions.add(qData);
            }
        }
        formData.put("questions", questions);

        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("form", formData);

        return ApiResponse.builder()
                .message("Get form success")
                .data(responseData)
                .build();
    }
}