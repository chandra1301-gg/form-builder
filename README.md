# Form Builder API

RESTful API for Dynamic Form Builder - Technical Assessment untuk **PT Hare Business Consulting**

## 🚀 Tech Stack

| Teknologi | Versi |
|-----------|-------|
| Java | 17 |
| Spring Boot | 3.2.5 |
| PostgreSQL | - |
| JWT (jjwt) | 0.12.5 |
| Spring Security | 6.x |
| Swagger (Springdoc) | 2.5.0 |
| Lombok | - |

---

## Cara Menjalankan
1. Clone repository
2. Buat database PostgreSQL: `form_builder`
3. Update `application.properties`
4. Jalankan: `./mvnw spring-boot:run`
5. Buka Swagger: http://localhost:8080/swagger-ui.html

## Default Users
| Name   | Email                 | Password   |
|--------|-----------------------|------------|
| User 1 | user1@webtech.id      | password1  |
| User 2 | user2@webtech.id      | password2  |
| User 3 | user3@worldskills.org | password3  |

## ✨ Fitur Utama

### 1. Authentication
- ✅ Login dengan JWT token
- ✅ Logout (stateless)
- ✅ Validasi input (422)
- ✅ Error handling (401)

### 2. Form Management
- ✅ Create form dengan allowed domains
- ✅ Slug unique validation
- ✅ Get all forms (milik user)
- ✅ Get form detail (dengan questions)

### 3. Question Management
- ✅ 6 tipe pertanyaan:
  - Short Answer (TextField)
  - Paragraph (TextArea)
  - Date (Input Date)
  - Multiple Choice (Radio)
  - Dropdown (Select)
  - Checkboxes
- ✅ Add & Remove questions
- ✅ Validasi choice_type & choices

### 4. Response Management
- ✅ Submit response (single & multi value)
- ✅ Domain-based access control
- ✅ Limit 1 response per form
- ✅ Get all responses (creator only)

### 5. Error Handling
- ✅ 200 - Success
- ✅ 401 - Unauthenticated
- ✅ 403 - Forbidden Access
- ✅ 404 - Not Found
- ✅ 422 - Invalid Field

---

## 📊 Entity Relationship Diagram (ERD)

![ERD](src/main/resources/static/erd.png)

### Daftar Tabel

| No | Tabel | Deskripsi |
|----|-------|-----------|
| 1 | `users` | Data user/pengguna |
| 2 | `forms` | Data form yang dibuat user |
| 3 | `allowed_domains` | Domain email yang diizinkan submit form |
| 4 | `questions` | Pertanyaan dalam form (6 tipe) |
| 5 | `responses` | Respons/jawaban form dari user |
| 6 | `answers` | Detail jawaban per pertanyaan |

### Relasi Antar Tabel

| Relasi | Dari | Ke | Keterangan |
|--------|------|----|------------|
| 1 : N | `users` | `forms` | Satu user membuat banyak form |
| 1 : N | `forms` | `allowed_domains` | Satu form punya banyak domain |
| 1 : N | `forms` | `questions` | Satu form punya banyak pertanyaan |
| 1 : N | `forms` | `responses` | Satu form punya banyak respons |
| 1 : N | `users` | `responses` | Satu user mengirim banyak respons |
| 1 : N | `responses` | `answers` | Satu respons punya banyak jawaban |
| 1 : N | `questions` | `answers` | Satu pertanyaan dijawab di banyak respons |

### Diagram Relasi (Mermaid)

```mermaid
erDiagram
    users ||--o{ forms : creates
    forms ||--o{ allowed_domains : has
    forms ||--o{ questions : has
    forms ||--o{ responses : receives
    users ||--o{ responses : submits
    responses ||--o{ answers : contains
    
    users {
        bigint id PK
        string name
        string email
        string password
        timestamp created_at
        timestamp updated_at
    }
    
    forms {
        bigint id PK
        string name
        string slug
        text description
        boolean limit_one_response
        bigint creator_id FK
        timestamp created_at
        timestamp updated_at
    }
    
    allowed_domains {
        bigint id PK
        string domain
        bigint form_id FK
    }
    
    questions {
        bigint id PK
        string name
        string choice_type
        text choices
        boolean is_required
        bigint form_id FK
        timestamp created_at
        timestamp updated_at
    }
    
    responses {
        bigint id PK
        bigint form_id FK
        bigint user_id FK
        datetime date
        timestamp created_at
        timestamp updated_at
    }
    
    answers {
        bigint id PK
        bigint question_id FK
        text value
        bigint response_id FK
    }
