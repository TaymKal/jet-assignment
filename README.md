# Restaurant Discovery CLI

## Setup

### Prerequisites

- **JDK 21 or newer** (required by this project; see `java.version` in `pom.xml`). Verify with `java -version`.
- **Network access** on the first run: the Maven Wrapper may download Apache Maven (see `distributionUrl` in `.mvn/wrapper/maven-wrapper.properties`).

### Maven Wrapper vs local Maven

Use the **Maven Wrapper** scripts in the repo root (`mvnw` / `mvnw.cmd`) so you do not need Maven installed globally. The wrapper pins the Maven version for this project.

If you already have **Apache Maven installed** and on your `PATH`, you can run the same goals with `mvn` instead of `mvnw` / `mvnw.cmd` (for example `mvn test` instead of `./mvnw.cmd test`).

### Build

- Windows (PowerShell / cmd, from repo root): `.\mvnw.cmd clean package`  
- macOS / Linux: `./mvnw clean package`  
- With local Maven: `mvn clean package`

### Run

- Windows: `.\mvnw.cmd spring-boot:run`  
- macOS / Linux: `./mvnw spring-boot:run`  
- With local Maven: `mvn spring-boot:run`

When prompted, enter a UK postcode (example: `EC4M7RF`) or type `exit` to quit.

### Example output

```text
Enter UK postcode (or type 'exit' to quit): EC4M7RF

Here are 10 restaurants for EC4M7RF:

1. Test Pizza | Rating: 4.5 | Cuisines: [Pizza] | Address: 10 Example Street, EC4M7RF
```

### Tests

- Windows: `.\mvnw.cmd test`  
- macOS / Linux: `./mvnw test`  
- With local Maven: `mvn test`

Run a single test class (example):

- Windows: `.\mvnw.cmd -Dtest=RestaurantCliRunnerTest test`  
- macOS / Linux: `./mvnw -Dtest=RestaurantCliRunnerTest test`  
- With local Maven: `mvn -Dtest=RestaurantCliRunnerTest test`

## Project Summary

This is a Spring Boot command-line application that fetches restaurant data from the Just Eat API for a UK postcode and displays the first 10 returned restaurants with:

- Name
- Cuisines
- Rating
- Address

The goal of this implementation is to keep the code clear, testable, and easy to extend if requirements evolve.

## Design and Architecture Choices

I implemented the solution as a layered Spring Boot CLI application, separating responsibilities across packages such as `cli`, `service`, `validator`, and `model`.

This keeps concerns isolated:

- CLI input/output flow in the runner layer
- Business and integration logic in the service layer
- Input validation in a dedicated validator
- API payload mapping in model classes

For this assignment scope, I used a synchronous approach for clarity and maintainability. For production scenarios with higher concurrency requirements, I would evaluate a reactive approach using Spring WebFlux.

## Assumptions

- The assignment asks to display the first 10 restaurants returned, so no additional sorting logic is applied.
- Since execution flow is not strictly specified, I implemented a prompt loop so users can query multiple postcodes without restarting the app.
- Live API responses can change over time (non-deterministic), so tests focus on deterministic behavior using mocks and `MockRestServiceServer` instead of asserting fixed live payloads.

## Why CLI for This Assignment

I chose a command-line interface to focus on backend correctness, input handling, integration behavior, and testability within the assignment scope.

For this size of application, a simple web UI would also be feasible, but CLI provided a faster path to demonstrate clean architecture, robustness, and automated testing.

## Error Handling

The application handles common failure scenarios explicitly, including:

- Invalid postcode input
- Upstream client-side API failures (e.g., bad request / not found)
- Upstream server-side failures (5xx)
- Transport/runtime errors when calling the API
- Empty or missing restaurant payloads

These are mapped to domain-specific exceptions and user-friendly CLI messages.

## Testing Strategy

I used a layered testing approach:

- Unit tests for postcode validation
- Unit tests for service logic with mocked dependencies
- Integration-style HTTP tests with `MockRestServiceServer` for request/response behavior and JSON mapping
- CLI behavior tests using in-memory input/output streams

This gives deterministic and reliable test coverage without depending on live API stability.

## Trade-offs

- Focused on readability and reliability over advanced patterns that were not required for the current scope.
- Given the assignment scope, I prioritized correctness, readability, and testability over production-scale concerns such as retries and caching.

## Future Improvements

- Add retry/backoff strategy for transient upstream failures.
- Add short-lived caching for repeated postcode lookups to reduce API calls.
- Add pagination/sorting controls if output requirements expand.

## AI Tool Usage

- Used AI primarily in Ask mode for guidance and debugging support, not to generate solutions.
- AI assisted with Spring Boot CLI approach, graceful shutdown configuration, and Mockito test syntax.
- I implemented the core architecture and application logic myself, and all AI-assisted changes were reviewed and validated locally.

