# FoodFlow - CSE3063F26 Team 4

FoodFlow is a console-based food ordering application built as the CSE3063 (Object-Oriented Software Design) term project at Marmara University. It brings Customers, Restaurants and Couriers together on one platform and keeps every order traceable from placement to delivery.

## Prerequisites

| Tool  | Version |
|-------|-------- |
| Java  | JDK 21 |
| Maven | 3.9.x |

## Build, run and test

Run all commands from the repository root.

| Task                        | Command                       |
|-----------------------------|-------------------------------|
| Compile                     | `mvn compile`                 |
| Run the console application | `mvn -q compile exec:java`    |
| Run the unit tests (JUnit)  | `mvn test`                    |
| Build the JAR (runs tests)  | `mvn package`                 |
| Clean build outputs         | `mvn clean`                   |

`mvn package` writes `target/foodflow-1.0-SNAPSHOT.jar`. The same `mvn -B package` command runs on every push and pull request to `main` through GitHub Actions.

## Project structure

```
.
├── data/
│   └── restaurants.json          # Restaurant and menu data
├── docs/                         # Vision, use cases, glossary, iteration plans, UML diagrams
├── src/main/java/edu/marmara/cse3063/foodflow/
│   ├── domain/                   # Core business entities
│   ├── application/              # Use case services and DTOs
│   ├── infrastructure/           # Repositories and JSON loading
│   └── ui/                       # Console UI
└── pom.xml
```

## Layers and packages

FoodFlow follows a traditional layered architecture. All packages live under `edu.marmara.cse3063.foodflow`. Each layer has a `LAYER-DEFINITION.md` file with its rules.

```
ui  ──►  application  ──►  infrastructure  ──►  domain
                 └───────────────────────────────►  domain
```

### `domain`
Holds the core business models

### `application`
Implements the use cases as service methods.

### `infrastructure`
Handles data access.

### `ui`
The console user interface.

## Documentation

- [Vision and scope](docs/vision.md)
- [Use cases](docs/use-cases.md)
- [Supplementary specification](docs/supplementary-spec.md)
- [Glossary](docs/glossary.md)
- [Iteration plan](docs/iteration-plan.md)
- [AI usage log](docs/ai-usage-log.md)
- [UML diagrams](docs/diagrams/)
