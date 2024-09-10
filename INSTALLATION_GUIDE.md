## Index

1. [**Prerequisites**](#prerequisites)
2. [**Technological Choices**](#technological-choices)
3. [**Running the Application**](#running-the-application)
4. [**Testing the API**](#testing-the-api)
    1. [Get all teams (without sorting)](#1-get-all-teams-without-sorting)
    2. [Get all teams (with sorting)](#2-get-all-teams-with-sorting)
        - [Sort by name in ascending order](#sort-by-name-in-ascending-order)
        - [Sort by name in descending order and budget in ascending order](#sort-by-name-in-descending-order-and-budget-in-ascending-order)
    3. [Get team by ID](#3-get-team-by-id)
        - [Example success response](#example-success-response)
        - [Example error response (Team Not Found)](#example-error-response-team-not-found)
    4. [Create a new team](#4-create-a-new-team)
        - [Request body](#request-body)
        - [Example success response](#example-success-response-1)
        - [Example error response (Validation Errors)](#example-error-response-validation-errors)
5. [**Running Tests**](#running-tests)
6. [**Connecting to H2 Database**](#connecting-to-h2-database)



## Prerequisites
Java 17 or higher

## Technological Choices

- Spring Boot
- H2 Database (and in-memory database for testing) 
- Spring Data JPA
- Spring Boot Starter Validation (for input validation)
- dependency management: Gradle
- logging: SLF4J (Logback) 
- Jackson (for JSON serialization/deserialization)
- JUnit 5 (for unit testing)

## Running the Application
To start the application, run the following command:
```bash
./gradlew bootRun
```
#### For windows
```bash
gradlew.bat bootRun
```

## Testing the API

### 1. Get all teams (without sorting)

```bash
GET http://localhost:8080/api/teams?page=0&size=5
```

### 2. Get all teams (with sorting)
#### Sort by name in ascending order
```bash
GET http://localhost:8080/api/teams?sortBy=name
```
#### Sort by name in descending order and build in ascending order
```bash
GET http://localhost:8080/api/teams?page=0&size=10&sortBy=-budget,name
```

### 3. Get team by id
```bash
GET http://localhost:8080/api/teams/{id}
```
#### Example success response
```json
{
  "id": 1,
  "name": "OGC Nice",
  "acronym": "OGCN",
  "budget": 50000000,
  "players": [
    {
      "id": 1,
      "name": "Player 1",
      "position": "Midfielder"
    },
    {
      "id": 2,
      "name": "Player 2",
      "position": "Defender"
    }
  ]
}
```
#### Example Error Response (Team Not Found):
```json
{
  "timestamp": "2024-09-10T14:30:00",
  "status": 404,
  "message": "Team with id 9999 not found"
}
```

### 4. Create a new team
```bash
POST http://localhost:8080/api/teams
```
#### Request body
```json
{
  "name": "OGC Nice",
  "acronym": "OGCN",
  "budget": 50000000,
  "players": [
    {
      "name": "Player 1",
      "position": "Midfielder"
    },
    {
      "name": "Player 2",
      "position": "Defender"
    }
  ]
}
```
#### Example Success Response:
```json
{
  "id": 1,
  "name": "OGC Nice",
  "acronym": "OGCN",
  "budget": 50000000,
  "players": [
    {
      "id": 1,
      "name": "Player 1",
      "position": "Midfielder"
    },
    {
      "id": 2,
      "name": "Player 2",
      "position": "Defender"
    }
  ]
}
```
#### Example Error Response (Validation Errors):
If required fields are missing or invalid
```json
{
  "timestamp": "2024-09-10T14:30:00",
  "status": 400,
  "errors": {
    "name": "Team name is required",
    "budget": "Budget must be a positive value"
  }
}

```

## Running Tests
```bash
./gradlew test
```
#### For windows
```bash
gradlew.bat test
```

## Connecting to H2 Database
```
http://localhost:8080/h2-console
```
- JDBC URL: jdbc:h2:~/equipe-football
- User Name: sa
- Password: (empty)

*Note: The test database is in-memory and will be destroyed after the application is stopped.*
