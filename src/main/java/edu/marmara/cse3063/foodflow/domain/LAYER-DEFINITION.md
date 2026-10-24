# Domain Layer

## Purpose

Defines the application's core business models. It is shared and used by Infrastructure and Application layers.

## Responsibilities

- Define entities.
- Represent entity properties and relationships between entities.
- Enforce rules related to an entity's own state.

## Dependency Rules

- Does not depend on any layer.

Application uses Domain entities when executing business logic. It converts the results returned to the UI into its own DTOs.

Domain objects do not load or save their own data; data access is the responsibility of Infrastructure.

## Naming Conventions

- Package name: `edu.marmara.cse3063.foodflow.domain`
- Use `PascalCase` for class names and `camelCase` for method and field names.