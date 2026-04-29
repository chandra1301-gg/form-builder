package com.hare.formbuilder.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.tags.*;
import org.springframework.context.annotation.*;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Form Builder API")
                        .version("1.0")
                        .description("""
                            RESTful API for Dynamic Form Builder
                            
                            ## Fitur Utama
                            - **Authentication** - Login/Logout dengan JWT
                            - **Form Management** - Membuat dan mengelola form dinamis
                            - **Question Management** - 6 tipe pertanyaan (Short Answer, Paragraph, Date, Multiple Choice, Dropdown, Checkboxes)
                            - **Response Management** - Submit dan melihat respons form
                            - **Domain Access Control** - Membatasi akses form berdasarkan domain email
                            - **Limit One Response** - Mencegah user submit lebih dari satu kali
                            
                            ## User untuk Testing
                            | Email | Password |
                            |-------|----------|
                            | user1@webtech.id | password1 |
                            | user2@webtech.id | password2 |
                            | user3@worldskills.org | password3 |
                            
                            ## Cara Testing
                            1. Login dengan endpoint `/api/v1/auth/login`
                            2. Copy token dari response
                            3. Klik tombol **Authorize** di kanan atas
                            4. Masukkan `<token>`
                            5. Testing endpoint-endpoint lainnya
                            """)
                        .contact(new Contact()
                                .name("Chandra Sampetua Manik")
                                .email("chandra080403@email.com")
                                .url("https://github.com/chandra1301-gg")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Masukkan token JWT dari endpoint login. Format: <token>")))
                .tags(List.of(
                        new Tag().name("1. Authentication").description("Login & Logout endpoints"),
                        new Tag().name("2. Form Management").description("CRUD Form endpoints"),
                        new Tag().name("3. Question Management").description("Add & Remove Question endpoints"),
                        new Tag().name("4. Response Management").description("Submit & View Response endpoints")
                ));
    }
}