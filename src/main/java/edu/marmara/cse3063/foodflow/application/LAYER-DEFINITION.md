# Application Layer

## Purpose

Defines the application's business workflows and services. It processes requests from the UI, coordinates the necessary data access operations, and returns results as DTOs.

## Responsibilities

- Implement use cases through service methods.
- Validate input data and conditions related to operations.
- Apply business rules by working with Domain objects.
- Define the repository interfaces (`application.repositories`) used for data access.
- Convert input DTOs into Domain objects and resulting entities into output DTOs.

## Dependency Rules

- Depends on the Domain layer.
- Repository interfaces are defined in `application.repositories` and implemented in Infrastructure.
- Repository implementations are not instantiated inside services; they are injected through constructors.

This project follows a traditional layered architecture. The Application layer defines the repository contracts it needs, and the Infrastructure layer implements them, so Application does not depend on Infrastructure.

## DTO Usage
DTO means Data Transfer Object

- DTOs are the layer between domain entities and the UI.
- Structured data received from the UI is represented by input DTOs.
- Structured results provided to the UI are represented by output DTOs.

Application retrieves Domain entities from repositories and converts them into the appropriate DTOs before returning them to the UI.

## Boundaries

- Does not read JSON files directly or use JSON processing libraries.
- Does not directly manage data storage structures.
- Does not read input from or write output to the console.
- Does not expose repositories or Domain entities to the UI.