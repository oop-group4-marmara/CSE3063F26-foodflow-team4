# Infrastructure Layer

## Purpose

Handles the application's data operations. It implements the repository interfaces defined in `application.repositories`.

## Responsibilities

- Load data from a JSON file at application startup.
- Keep the loaded data in memory to handle queries.
- Provide read-only operations for data.
- Store customers in memory throughout the application's runtime.
- Manage JSON-specific data structures and their conversion into Domain entities.

The JSON file is not read again during restaurant queries. Customer data stored in memory is lost when the application terminates.

## Dependency Rules

- Depends on the Domain layer and on the repository contracts in `application.repositories`.
- May use libraries required for technical operations such as JSON processing and file access.
- Loads and stores data as Domain entities.