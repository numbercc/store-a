# Inventory Management System
The Inventory Management System is a Kotlin web application built with Spring Boot. It allows users to manage the inventory of products in a specific building supply store (bouwmarkt). The application provides a RESTful API for CRUD operations on products and their reservations.

# Features
- CRUD API for managing products in the inventory.
- Reserve stock of a product for a specific period (5 minutes) before it automatically expires.
- Confirm and decrease reserved stock after a reservation is confirmed.
- Additional functionalities include test coverage, API documentation, and authorization on CRUD operations (not implemented in the provided code snippet).
# Possible Feature Enhancements
Here are some feature possibilities that can further enhance the functionality and capabilities of the Inventory Management System:

- OpenAPI Implementation (Contract-First Approach): Adopt the contract-first approach using OpenAPI Specification (OAS) to design and document the API. Create an API specification in the OpenAPI format that defines the endpoints, request/response formats, data types, and other relevant details. Use tools like Swagger Editor or Swagger UI to interactively edit and visualize the API specification. Generate server-side code and documentation from the specification using tools like Swagger Codegen or SpringDoc. This approach promotes collaboration, ensures consistency between the specification and implementation, and simplifies API consumption for clients.
- Validation for Parameters: Enhance the controllers to include parameter validation. Use annotations like @Valid and @RequestParam to enforce constraints on input data, ensuring that incoming requests contain valid and expected data.
- Authentication and Authorization: Implement authentication and authorization mechanisms to secure the API endpoints. Utilize Spring Security to handle user authentication and control access to specific endpoints based on user roles and permissions.
- Monitor Metrics: Integrate monitoring and metrics collection to gain insights into the application's performance and usage. Consider using tools like Micrometer and Spring Boot Actuator to collect metrics on request rates, response times, error rates, etc.

# Technologies Used
Spring Boot
Kotlin
Gradle
Hibernate (JPA)
H2 Database (in-memory)
Jackson (for JSON serialization/deserialization)
Spring Web (for building RESTful APIs)
Spring Data JPA (for data access and persistence)
Spring Test (for integration testing)
Setup
To run the application locally, follow the steps mentioned in the previous section of the README.

# Setup
To run the application locally, follow these steps:

Make sure you have Java 17 or later installed on your machine.
Clone the repository to your local machine.
````
bash
git clone https://github.com/your-username/inventory-management.git
cd inventory-management
````
Build the application using Gradle.
````
bash

./gradlew build
````
Run the application.
````
bash
./gradlew bootRun
````
The application should now be running locally on http://localhost:8080.

# API Endpoints
##  ProductController
- POST /products/: Create a new product in the inventory. Requires productCode and stock as parameters.
- POST /products/{productCode}/add-stock: Add stock to an existing product. Requires productCode as a path variable and quantity as a request parameter.
- POST /products/{productCode}/decrease-stock: Decrease stock of an existing product. Requires productCode as a path variable and quantity as a request parameter.
- GET /products/: Get a product by its productCode. Requires productCode as a query parameter.
- DELETE /products/{productCode}: Delete an existing product from the inventory. Requires productCode as a path variable.

## ReservationController
- POST /reservations/{productCode}/reserve: Reserve stock of a product. Requires productCode as a path variable and quantity as a request parameter.
- POST /reservations/{articleReservationId}/confirm: Confirm a reservation. Requires articleReservationId as a path variable.

# Integration Tests
Integration tests have been included to ensure the correctness of the application's endpoints and functionalities. The tests use the Spring Boot Test framework and the MockMvc object to perform HTTP requests and check the responses.

To run the integration tests, execute the following command:


````
bash
./gradlew test
````
The tests will start the Spring Boot application in a test environment and verify the behavior of the API endpoints.

# Conclusion
The Inventory Management System is a Kotlin web application built with Spring Boot that allows users to manage the inventory of products in a building supply store. It provides a CRUD API for products, stock reservation, and confirmation functionalities. The application also includes integration tests to ensure its correctness.