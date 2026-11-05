# UI Layer

The console UI displays menus, parses selections, calls Application service interfaces and prints their results. It handles expected validation failures with friendly messages and keeps navigation running.

- No business rules, repository access, JSON loading or Domain entity imports belong here.
- Structured service inputs and outputs use Application DTOs.
- `ConsoleInput` reads complete lines from one injected scanner. It does not create or close the reader.
- `ConsoleUI` owns navigation, including form cancellation and normal end-of-input handling.
- The root-package `App` creates repositories and services, connects them to the UI and manages the scanner lifetime.
