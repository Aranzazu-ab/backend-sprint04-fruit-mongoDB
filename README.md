# Fruit Order REST API (MongoDB)

A RESTful API built with Spring Boot to manage customer fruit orders using MongoDB as a document-oriented persistence system. This service allows clients to place orders with delivery scheduling and fruit selections, while enabling order managers to track, update, and cancel orders.

The application connects to a MongoDB database using Spring Data MongoDB, models data using embedded documents, validates incoming requests using Bean Validation, provides centralized exception handling, and is containerized using Docker and Docker Compose.

---

## Business Logic and Practical Example

The core of this application revolves around the `Order` aggregate document and its embedded `OrderItem` subdocuments. Instead of distributing items across multiple relational tables, MongoDB stores each order as a single self-contained document in the `orders` collection.

### Domain Rules:
- An order requires a client name, a valid delivery date, and at least one ordered fruit item.
- The delivery date must be scheduled for the future (at least tomorrow). Past dates or the current day are rejected.
- Each item must specify a fruit name and a strictly positive quantity in kilos.

### Practical Workflow Walkthrough

Here is a common scenario showing how the API behaves:

#### 1. Create a New Order
A customer places an order specifying delivery date and items.

- **Request:** `POST /orders`
```json
{
  "clientName": "Alicia Aguilar",
  "deliveryDate": "2026-11-26",
  "items": [
    {
      "fruitName": "Strawberries",
      "quantityInKilos": 3
    },
    {
      "fruitName": "Bananas",
      "quantityInKilos": 5
    }
  ]
}
```
- **Response:** `201 Created`
```json
{
  "id": "674251a37c95b430d47d010a",
  "clientName": "Alicia Aguilar",
  "deliveryDate": "2026-11-26",
  "items": [
    {
      "fruitName": "Strawberries",
      "quantityInKilos": 3
    },
    {
      "fruitName": "Bananas",
      "quantityInKilos": 5
    }
  ]
}
```

#### 2. Input Validation Handling
If an order is submitted with an invalid delivery date (e.g., today or in the past) or invalid item quantities, the API rejects the request before hitting the database.

- **Request:** `POST /orders`
```json
{
  "clientName": "",
  "deliveryDate": "2020-01-01",
  "items": [
    {
      "fruitName": "Apples",
      "quantityInKilos": 0
    }
  ]
}
```
- **Response:** `400 Bad Request`
```json
{
  "timestamp": "2026-11-23T23:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "errors": {
    "clientName": "Client name is required",
    "deliveryDate": "Delivery date must be at least tomorrow",
    "items[0].quantityInKilos": "Quantity must be greater than zero"
  }
}
```

#### 3. Consult All Orders
Order managers can retrieve the entire list of registered orders. If no orders exist, an empty list is returned.

- **Request:** `GET /orders`
- **Response:** `200 OK`
```json
[
  {
    "id": "674251a37c95b430d47d010a",
    "clientName": "Alicia Aguilar",
    "deliveryDate": "2026-11-26",
    "items": [
      {
        "fruitName": "Strawberries",
        "quantityInKilos": 3
      },
      {
        "fruitName": "Bananas",
        "quantityInKilos": 5
      }
    ]
  }
]
```

#### 4. Consult an Order by ID
Retrieve details of a specific order using its MongoDB identifier.

- **Request:** `GET /orders/674251a37c95b430d47d010a`
- **Response:** `200 OK`
```json
{
  "id": "674251a37c95b430d47d010a",
  "clientName": "Alicia Aguilar",
  "deliveryDate": "2026-11-26",
  "items": [
    {
      "fruitName": "Strawberries",
      "quantityInKilos": 3
    },
    {
      "fruitName": "Bananas",
      "quantityInKilos": 5
    }
  ]
}
```
If the ID does not exist, the API returns `404 Not Found`.

#### 5. Modify an Existing Order
Clients can modify their orders if changes or corrections are needed.

- **Request:** `PUT /orders/674251a37c95b430d47d010a`
```json
{
  "clientName": "Alicia Aguilar",
  "deliveryDate": "2026-11-27",
  "items": [
    {
      "fruitName": "Blueberries",
      "quantityInKilos": 2
    }
  ]
}
```
- **Response:** `200 OK`
```json
{
  "id": "674251a37c95b430d47d010a",
  "clientName": "Alicia Aguilar",
  "deliveryDate": "2026-11-27",
  "items": [
    {
      "fruitName": "Blueberries",
      "quantityInKilos": 2
    }
  ]
}
```

#### 6. Cancel or Delete an Order
Orders can be removed when cancelled.

- **Request:** `DELETE /orders/674251a37c95b430d47d010a`
- **Response:** `204 No Content`

If the order ID does not exist, the API returns `404 Not Found`.

---

## Tech Stack

- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3
- **Data & Persistence:** Spring Data MongoDB, MongoDB 6.0
- **Validation:** Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Positive`, `@NotNull`, `@Future`, `@NotEmpty`)
- **Testing:** JUnit 5, Mockito, Spring Boot Test, MockMvc
- **Containerization:** Docker (multi-stage build), Docker Compose
- **Build Tool:** Maven

---

## Project Architecture

The project follows a standard layered architecture with clear separation of concerns:

- `controllers`: REST endpoints handling incoming HTTP requests, response status codes, and path routing.
- `service`: Business logic, domain rule validations, and coordination between repository and mappers.
- `repository`: Spring Data MongoDB repository interface (`OrderRepository`) handling persistence operations.
- `model`: Document definitions (`Order`) and embedded subdocuments (`OrderItem`).
- `dtos`: Request and response record objects ensuring internal domain entities are never exposed directly.
- `mapper`: Mapping components converting between entities and DTOs.
- `exceptions`: Custom domain exceptions (e.g., `OrderNotFoundException`) and a `GlobalExceptionHandler` returning consistent, structured error responses.

---

## API Endpoints Reference

### Orders (`/orders`)

| Method | Endpoint | Description | Status Codes |
|---|---|---|---|
| POST | `/orders` | Create a new fruit order | 201 Created, 400 Bad Request |
| GET | `/orders` | List all registered orders | 200 OK |
| GET | `/orders/{id}` | Get order details by ID | 200 OK, 404 Not Found |
| PUT | `/orders/{id}` | Update an existing order by ID | 200 OK, 400 Bad Request, 404 Not Found |
| DELETE | `/orders/{id}` | Delete an order by ID | 204 No Content, 404 Not Found |

---

## Automated Testing

The project includes automated tests to verify business logic and API endpoints:

- **Unit Tests (`src/test/.../service`):** Built with JUnit 5 and Mockito. These isolate service logic by mocking repository interactions to test scenarios such as successful creation, order updates, delivery date validations, and missing order handling (`OrderNotFoundException`).
- **Controller Tests (`src/test/.../controllers`):** Built using Spring's `MockMvc`. These verify HTTP endpoint status codes, request serialization, validation constraint triggers, and error response structures.

To run the complete test suite:

```bash
./mvnw test
```

On Windows:

```cmd
.\mvnw.cmd test
```

---

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8+ (or use the included `./mvnw` wrapper)
- Docker and Docker Compose (recommended) or a local MongoDB instance

### Running with Docker Compose (Recommended)

1. Start the MongoDB container using Docker Compose:
   ```bash
   docker compose up -d
   ```
   This starts a MongoDB 6.0 instance accessible on port `27017` with a named volume for data persistence.

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
3. The API will be accessible at `http://localhost:8080`.

To stop the database container:
```bash
docker compose down
```

### Running with Multi-Stage Dockerfile

A production-ready multi-stage `Dockerfile` is provided:

1. Build the Docker image:
   ```bash
   docker build -t fruit-order-api .
   ```

2. Run the container:
   ```bash
   docker run -p 8080:8080 -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/orders_db fruit-order-api
   ```

---

## Environment Variables

When running in containerized or cloud environments, MongoDB connection parameters can be configured via environment variables:

| Variable | Description | Default (Local) |
|---|---|---|
| `SPRING_DATA_MONGODB_URI` | Connection URI for the MongoDB database | `mongodb://localhost:27017/orders_db` |
| `SERVER_PORT` | Port on which the Spring Boot application listens | `8080` |
