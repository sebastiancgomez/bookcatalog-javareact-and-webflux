# 📚 Book Catalog API (Reactive Microservice)

Reactive REST API for managing a book catalog built with **Spring Boot WebFlux**, **R2DBC**, and deployed in **Azure Container Apps**.

This project demonstrates modern backend engineering practices including:

- Reactive programming
- Clean architecture
- DTO projections
- Pagination and filtering
- Docker containerization
- Cloud deployment on Azure
- Reactive testing

---

# 🚀 Live API

The API is publicly available at:

https://bookcatalog-app.lemonflower-9dbaf244.canadacentral.azurecontainerapps.io/

### Swagger UI

API documentation:

```
https://bookcatalog-app.lemonflower-9dbaf244.canadacentral.azurecontainerapps.io/swagger-ui.html
```

⚠ **Important**

The container is configured with **scale-to-zero** in Azure Container Apps to reduce cost.

If the API has been inactive for a while, the first request may take **20–40 seconds** while the container instance starts.

---

# 🚀 Tech Stack

### Backend

- Java 17
- Spring Boot 3
- Spring WebFlux
- Spring Data R2DBC
- Project Reactor (Mono / Flux)
- Maven

### API

- REST
- OpenAPI / Swagger

### Testing

- JUnit 5
- Reactor Test (StepVerifier)
- WebTestClient
- Mockito

### Infrastructure

- Docker
- Azure Container Registry (ACR)
- Azure Container Apps

### Frontend (demo client)

- HTML
- CSS
- Vanilla JavaScript

---

# 🏗 Architecture

The service follows a layered architecture designed for reactive applications.

```
Client
   ↓
Spring WebFlux Controller
   ↓
Service Layer
   ↓
Repository Layer (Reactive R2DBC)
   ↓
Database
```

Supporting components:

```
DTO
Mapper
Global Exception Handler
Pagination Response
```

Reactive flow:

```
Controller
   ↓
Mono / Flux
   ↓
Service
   ↓
Repository
   ↓
Database
```

---

# ☁️ Cloud Architecture (Azure)

Deployment flow:

```
Developer
   ↓
Docker Build
   ↓
Azure Container Registry (ACR)
   ↓
Azure Container Apps
   ↓
Public Endpoint
```

Infrastructure components used:

```
Azure Resource Group
    ↓
Azure Container Registry
    ↓
Azure Container App Environment
    ↓
BookCatalog Container App
```

The container app automatically pulls the image from ACR.

---

# 📦 Project Structure

```
src
 ├─ controllers
 │   └─ BookController
 │
 ├─ config
 │   └─ OpenApiConfig
 │
 ├─ services
 │   ├─ BookService
 │   └─ BookServiceImpl
 │
 ├─ repository
 │   └─ BookRepository
 │
 ├─ model
 │   └─ Book
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
 │   ├─ BookNotFoundException
 │   ├─ ErrorResponse
 │   └─ GlobalExceptionHandler
 │
 └─ dto/response
     ├─ PageResponse    
     └─ PaginatedBooks
```

---

# 📚 API Features

### CRUD Operations

| Method | Endpoint | Description |
|------|------|------|
GET | `/books` | Get books with pagination and filters |
GET | `/books/{id}` | Get book by ID |
POST | `/books` | Create book |
PUT | `/books/{id}` | Update book |
DELETE | `/books/{id}` | Delete book |

---

# 🔎 Filtering

Supported filters:

```
GET /books?title=clean
GET /books?author=martin
GET /books?publishDateFrom=2024-01-01
GET /books?publishDateTo=2024-12-31
```

Filters can be combined.

---

# 📄 Pagination

Example request:

```
GET /books?page=0&size=5
```

Example response:

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

# 🧠 DTO Projection

The API supports multiple DTO projections to optimize payload size.

Example:

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

---

# 🧪 Testing

Testing strategy includes:

- **Controller tests** using WebTestClient
- **Service tests** using StepVerifier
- **Mocking** with Mockito

Example reactive test:

```java
StepVerifier.create(service.getById(1L))
    .expectNextMatches(book -> book.getTitle().equals("Clean Code"))
    .verifyComplete();
```

---

# 🐳 Running Locally with Docker

Build the image:

```
docker build -t bookcatalog-app .
```

Run container:

```
docker run -p 8080:8080 bookcatalog-app
```

Then open:

```
http://localhost:8080/swagger-ui.html
```

---

# ☁️ Azure Deployment

Deployment process:

```
1. Build Docker image
2. Push image to Azure Container Registry
3. Container App pulls image from ACR
4. Azure exposes the application through a public endpoint
```

Commands used during deployment:

```
az acr login --name bookcatalogacr
docker tag bookcatalog-app:latest bookcatalogacr.azurecr.io/bookcatalog-app:latest
docker push bookcatalogacr.azurecr.io/bookcatalog-app:latest
```

---

# 📊 Concepts Demonstrated

Reactive programming with Spring WebFlux

DTO projection strategies

Pagination and filtering

Reactive error handling

Clean architecture

Containerized deployment

Cloud-native architecture

Reactive unit testing

---

## 👨‍💻 Author

**Juan Sebastián Cárdenas Gómez**

Backend Engineer specialized in Java, Spring Boot, microservices, and reactive systems.

This project was built as part of backend architecture practice and cloud-native deployment experimentation using Azure Container Apps.

🔗 GitHub: https://github.com/sebastiancgomez  
🔗 LinkedIn: https://linkedin.com/in/juan-sebastian-cardenas-gomez-aa624731

