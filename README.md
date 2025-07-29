# Question Service

## 📘 Overview

A Spring Boot microservice for managing quiz questions. It provides REST APIs for creating, retrieving, updating, and deleting questions. Part of the **QuizApp** microservices suite.

---

## 🚀 Technologies Used

* Java 11+
* Spring Boot
* Spring Data MongoDB
* Maven
* MongoDB Atlas (Cloud-hosted NoSQL DB)
* Thymeleaf (optional)

---

## 📁 Project Structure

```
src/
 └── main/
     ├── java/
     │   └── com/example/question_service/
     │       ├── controller/          # REST controllers
     │       ├── entity/              # MongoDB entities
     │       ├── repository/          # Spring Data repositories
     │       └── service/             # Business logic
     └── resources/
         ├── application.properties  # App configuration
         ├── static/                 # Static resources (optional)
         └── templates/              # Thymeleaf templates (optional)
```

---

## 🛠️ Building and Running

### 📋 Prerequisites

* Java 11 or higher
* Maven 3.8+

### 🧪 Build

```bash
mvn clean install
```

### ▶️ Run

```bash
mvn spring-boot:run
```

> App will be available at: `http://localhost:8080`

---

## 📡 API Endpoints

Base path: `/questions`

| Method | Endpoint          | Description               |
| ------ | ----------------- | ------------------------- |
| GET    | `/questions`      | Retrieve all questions    |
| GET    | `/questions/{id}` | Retrieve a question by ID |
| POST   | `/questions`      | Add a new question        |
| PUT    | `/questions/{id}` | Update existing question  |
| DELETE | `/questions/{id}` | Delete a question         |

---

## 🧩 Entities

Located in `entity/`

* `Question.java` — core entity
* `QuestionWrapper.java` — wrapper for question sets
* `Response.java` — custom response class

---

## 🗃️ Repository

`repository/QuestionRepository.java`

* Extends `MongoRepository`
* Supports CRUD + derived queries

---

## 💼 Service Layer

`service/QuestionService.java`

* Handles business logic
* Delegates to repository layer

---

## ⚙️ Configuration

In `src/main/resources/application.properties`

Sample MongoDB Atlas config:

```properties
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster-url>/quizapp?retryWrites=true&w=majority
spring.data.mongodb.database=quizapp
```

> Replace `<username>`, `<password>`, and `<cluster-url>` with your actual MongoDB Atlas credentials.

---

## 🧪 Testing

Run unit/integration tests with:

```bash
mvn test
```

---

## 🤝 Contribution

1. Fork this repository
2. Create a feature branch: `git checkout -b feature/MyFeature`
3. Commit your changes
4. Push and submit a Pull Request

---

## 📄 License

Add license info here (e.g. MIT, Apache 2.0)

---

> 🌟 *Happy Coding from the QuizApp Team!*
