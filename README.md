[# 📚 Book Catalog API (Reactive Microservice)

Reactive REST API for managing a book catalog built with **Spring Boot WebFlux**, **R2DBC**, and deployed in **Azure Container Apps**.

This project demonstrates modern backend practices including reactive programming, clean architecture, Docker containerization, cloud deployment, and API documentation.

---

# 🚀 Tech Stack

Backend

- Java 17
- Spring Boot 3
- Spring WebFlux (Reactive)
- Spring Data R2DBC
- Reactor (Mono / Flux)
- Maven

API

- REST
- OpenAPI / Swagger

Testing

- JUnit 5
- Reactor Test (StepVerifier)
- WebTestClient
- Mockito

Infrastructure

- Docker
- Azure Container Registry (ACR)
- Azure Container Apps

Frontend (simple demo)

- HTML
- CSS
- Vanilla JavaScript

---

# 🏗 Architecture

The service follows a layered architecture:

```
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Additional layers:

```
DTO
Mapper
GlobalExceptionHandler
```

Reactive data flow:

```
Controller → Mono / Flux → Service → Repository → Database
```

---

# 📦 Project Structure

```
src
 ├─ controllers
 │   └─ BookController
 │
 ├─ services
 │   ├─ BookService
 │   └─ BookServiceImpl
 │
 ├─ repository
 │   └─ BookRepository
 │
 ├─ dto
 │   ├─ BookDto
 │   ├─ BookMinimalDto
 │   └─ BookFullDto
 │
 ├─ mapper
 │   └─ BookMapper
 │
 ├─ exceptions
 │   └─ GlobalExceptionHandler
 │
 └─ dto/response
     └─ PaginatedBooks
```

---

# 📚 API Features

## CRUD Operations

| Method | Endpoint | Description |
|------|------|------|
GET | `/books` | Get books (paginated + filters)
GET | `/books/{id}` | Get book by ID
POST | `/books` | Create book
PUT | `/books/{id}` | Update book
DELETE | `/books/{id}` | Delete book

---

# 🔎 Filtering

Supported filters:

```
GET /books?title=clean
GET /books?author=martin
GET /books?publishDateFrom=2024-01-01
GET /books?publishDateTo=2024-12-31
```

Combined filters are also supported.

---

# 📄 Pagination

Example:

```
GET /books?page=0&size=5
```

Response:

```json
{
  "total": 10,
  "books": [
    {
      "id": 1,
      "title": "Clean Code",
      "author": "Robert Martin"
    }
  ]
}
```

---

# 🧠 Dynamic DTO Projection

The API supports dynamic DTO responses.

```
GET /books?dto=minimal
GET /books?dto=full
```

### Minimal DTO

```
{
  "id": 1,
  "title": "Clean Code"
}
```

### Full DTO

```
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert Martin",
  "price": 40.00,
  "publishDate": "2008-08-01"
}
```

This reduces payload size and improves performance for list endpoints.

---

# 📑 API Documentation

Swagger UI is available at:

```
/swagger-ui.html
```

Example:

```
http://localhost:8080/swagger-ui.html
```

---

# 🧪 Testing

Tests include:

- Controller tests with **WebTestClient**
- Service tests with **StepVerifier**
- Mocking with **Mockito**

Example reactive test:

```java
StepVerifier.create(service.getById(1L))
    .expectNextMatches(book -> book.getTitle().equals("Clean Code"))
    .verifyComplete();
```

---

# 🐳 Running with Docker

Build the image:

```
docker build -t bookcatalog-app .
```

Run container:

```
docker run -p 8080:8080 bookcatalog-app
```

---

# ☁️ Azure Deployment

The application is deployed using:

- **Azure Container Registry**
- **Azure Container Apps**

Deployment flow:

```
Local Build
     ↓
Docker Image
     ↓
Push to Azure Container Registry
     ↓
Azure Container App pulls image
     ↓
Application exposed via public endpoint
```

---

# 🌐 Live API

Example deployment URL:

```
https://bookcatalog-app.azurecontainerapps.io
```

---

# 📊 Key Concepts Demonstrated

Reactive programming with WebFlux

DTO projection strategies

Reactive error handling

Pagination and filtering

Cloud-native container deployment

Clean service architecture

API documentation with OpenAPI

Unit testing reactive pipelines

---

# 👨‍💻 Author

Backend engineer specialized in:

- Java
- Spring Boot
- Microservices
- Cloud deployment
- Reactive systems

```
This project was built as part of backend architecture practice and cloud-native deployment experimentation.
```

---](https://www.linkedin.com/jobs/view/4377968599)
