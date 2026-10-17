Shopeasy – Order Management System



A backend REST API application developed using Java 21, Spring Boot, Spring Security, JWT, Spring Data JPA, MySQL, and Docker.



The application provides secure APIs for managing customers, products, and orders, with authentication, role-based authorization, business validations, and centralized exception handling.



Technologies Used



\- Language: Java 21

\- Framework: Spring Boot

\- Security: Spring Security, JWT

\- Persistence: Spring Data JPA, Hibernate

\- Database: MySQL 8

\- Build Tool: Maven

\- API Documentation: Swagger / OpenAPI

\- Containerization: Docker, Docker Compose

\- Version Control: Git, GitHub

\- Testing: JUnit, Mockito



Key Features



&#x20;Customer Management

\- Create, retrieve, update, and delete customer records.

\- Manage customer information through REST APIs.



&#x20;Product Management

\- Create, retrieve, update, and delete products.

\- Maintain product prices and available stock.

\- Restrict product modification operations to ADMIN users.



&#x20;Order Management

\- Place orders for customers.

\- Calculate order amounts based on product prices and quantities.

\- Validate available product stock.

\- Track and update order statuses.

\- Support order pagination and dynamic filtering.



&#x20;Authentication and Authorization

\- JWT-based authentication.

\- User registration and login.

\- Role-based access control using Spring Security.

\- Protected endpoints for administrative operations.



&#x20;Exception Handling and Validation

\- Centralized exception handling.

\- Structured API error responses.

\- Validation of incoming requests.

\- Appropriate HTTP status codes for business errors.



&#x20;API Documentation

\- Interactive API documentation using Swagger/OpenAPI.



&#x20;Docker Support

\- Dockerfile for containerizing the Spring Boot application.

\- Docker Compose configuration for running the application with MySQL.

\- Environment-based configuration for sensitive credentials.



&#x20;Project Structure



src/main/java/com/shopeasy/ordermanagement/

├── config/

├── controller/

├── dto/

│   ├── request/

│   └── response/

├── entity/

├── enums/

├── exception/

├── repository/

├── security/

├── service/

├── specification/

└── OrdermanagementApplication.java



Running the Application

&#x20;

&#x20;Prerequisites



\- Java 21

\- Maven

\- MySQL 8 or Docker Desktop

\- Git



&#x20;Clone the Repository



git clone https://github.com/amruthaanna2924-dot/shopeasy-order-management.git

cd shopeasy-order-management





&#x20;Configure Environment Variables



Configure the database connection and JWT secret using environment variables.



The application supports:



SPRING\_DATASOURCE\_URL=jdbc:mysql://localhost:3306/order\_management

SPRING\_DATASOURCE\_USERNAME=root

SPRING\_DATASOURCE\_PASSWORD=your\_database\_password

JWT\_SECRET=your\_long\_random\_jwt\_secret





For Docker Compose, create a `.env` file in the project root with `SPRING\_DATASOURCE\_PASSWORD` and `JWT\_SECRET`.



Do not commit the `.env` file or actual credentials to Git.



&#x20;Run Using Maven





./mvnw spring-boot:run





On Windows:





.\\mvnw.cmd spring-boot:run





&#x20;Run Using Docker Compose



Build the application JAR first:



.\\mvnw.cmd clean package





Build the Docker image:



docker build -t shopeasy-app:latest .





Start the containers:



docker compose up -d





The application is configured to run on:



http://localhost:8080



The MySQL container exposes port `3307` on the host.



&#x20;Health Check





GET http://localhost:8080/actuator/health





Stop Docker Containers



docker compose down





Testing



Unit tests are located in src/test/java



Run tests using:



.\\mvnw.cmd test



&#x20;Future Enhancements



\- Kafka-based asynchronous event processing.

\- Microservices architecture.

\- Expanded automated testing and integration tests.

\- CI/CD pipeline using GitHub Actions.

\- Improved monitoring and observability.



&#x20;Author



Developed as a Java backend portfolio project.



GitHub: https://github.com/amruthaanna2924-dot



