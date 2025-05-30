# SC Kill Monitor Developer Guidelines

This document provides essential information for new developers joining the SC Kill Monitor project. It offers guidance on project structure, running tests, executing scripts, and following best practices.

## Project Overview

SC Kill Monitor is a JavaFX application that searches Star Citizen game logs to identify players who killed you. The application is built with Java and uses Gradle for build automation.

## Project Structure

```
sckillmonitor/
├── .junie/              # Project-specific guidelines
├── config/              # Configuration files
│   ├── checkstyle/      # Code style configuration
│   └── settings/        # Application settings
├── docs/                # Documentation and generated files
├── gradle/              # Gradle wrapper files
├── src/
│   ├── main/
│   │   ├── java/        # Main source code
│   │   │   └── de/greluc/sc/sckm/  # Main package
│   │   └── resources/   # Application resources
│   └── test/
│       ├── java/        # Test source code
│       └── resources/   # Test resources
└── build.gradle.kts     # Gradle build configuration
```

## Tech Stack

- **Java**: Core programming language
- **JavaFX**: UI framework
- **Gradle**: Build tool with Kotlin DSL
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework for tests
- **Log4j2**: Logging framework
- **Jackson**: JSON processing
- **AtlantaFX**: UI theme library
- **Lombok**: Reduces boilerplate code

## Building the Project

To build the project:

```
./gradlew build
```

This will compile the code, run tests, and generate reports.

## Running Tests

Run all tests:

```
./gradlew test
```

Run specific tests:

```
./gradlew test --tests "de.greluc.sc.sckm.settings.UuidTest"
```

Test reports are generated in `build/reports/tests/test/`.

## Running the Application

Run the application:

```
./gradlew run
```

## Creating Distributable Packages

Create platform-specific packages:

```
./gradlew jpackage
```

This creates installable packages in `build/jpackage/`.

## Code Style and Best Practices

1. **Follow Google's Java Style Guide**: The project uses [Google's Java Style Guide](https://google.github.io/styleguide/javaguide.html). Configuration is available in the `config/checkstyle` directory.

2. **Boilerplate code**: Reduce boilerplate code by using the [Project Lombok](https://projectlombok.org/).

3. **Checkstyle**: Ensure that Checkstyle does not throw any warnings or errors.

4. **JavaDoc Comments**: Always include JavaDoc for classes and methods:
   - Update the `@author` tag while retaining earlier authors
   - Use the `@since` tag for new additions

5. **Logging**: Use Log4j2 via Lombok's `@Log4j2` annotation for logging.

6. **Git Workflow**:
   - Create branches from `develop`
   - Use present tense in commit messages
   - Reference issues in commit messages
   - Submit pull requests against the `develop` branch

7. **Testing**:
   - Write unit tests for new functionality
   - Ensure all tests pass before submitting code
   - Use JUnit 5 assertions and Mockito for mocking

## Documentation

- **Code of Conduct**: Read `CODE_OF_CONDUCT.md` before contributing
- **Contributing Guidelines**: See `CONTRIBUTING.md` for detailed contribution process
- **Wiki**: Check the project wiki for installation and operating manuals

## Common Issues and Solutions

- If you encounter build errors, ensure you're using Java 24
- For UI issues, check the FXML files in the resources directory
- Log files are useful for debugging runtime issues

## Getting Help

If you need assistance, create an issue with the label "support" after checking existing issues and documentation.

# Spring Boot Guidelines

## 1. Prefer Constructor Injection over Field/Setter Injection
* Declare all the mandatory dependencies as `final` fields and inject them through the constructor.
* Spring will auto-detect if there is only one constructor, no need to add `@Autowired` on the constructor.
* Avoid field/setter injection in production code.

## 2. Prefer package-private over public for Spring components
* Declare Controllers, their request-handling methods, `@Configuration` classes and `@Bean` methods with default (package-private) visibility whenever possible. There's no obligation to make everything `public`.

## 3. Organize Configuration with Typed Properties
* Group application-specific configuration properties with a common prefix in `application.properties` or `.yml`.
* Bind them to `@ConfigurationProperties` classes with validation annotations so that the application will fail fast if the configuration is invalid.
* Prefer environment variables instead of profiles for passing different configuration properties for different environments.

## 4. Define Clear Transaction Boundaries
* Define each Service-layer method as a transactional unit.
* Annotate query-only methods with `@Transactional(readOnly = true)`.
* Annotate data-modifying methods with `@Transactional`.
* Limit the code inside each transaction to the smallest necessary scope.


## 5. Disable Open Session in View Pattern
* While using Spring Data JPA, disable the Open Session in View filter by setting ` spring.jpa.open-in-view=false` in `application.properties/yml.`

## 6. Separate Web Layer from Persistence Layer
* Don't expose entities directly as responses in controllers.
* Define explicit request and response record (DTO) classes instead.
* Apply Jakarta Validation annotations on your request records to enforce input rules.

## 7. Follow REST API Design Principles
* **Versioned, resource-oriented URLs:** Structure your endpoints as `/api/v{version}/resources` (e.g. `/api/v1/orders`).
* **Consistent patterns for collections and sub-resources:** Keep URL conventions uniform (for example, `/posts` for posts collection and `/posts/{slug}/comments` for comments of a specific post).
* **Explicit HTTP status codes via ResponseEntity:** Use `ResponseEntity<T>` to return the correct status (e.g. 200 OK, 201 Created, 404 Not Found) along with the response body.
* Use pagination for collection resources that may contain an unbounded number of items.
* The JSON payload must use a JSON object as a top-level data structure to allow for future extension.
* Use snake_case or camelCase for JSON property names consistently.

## 8. Use Command Objects for Business Operations
* Create purpose-built command records (e.g., `CreateOrderCommand`) to wrap input data.
* Accept these commands in your service methods to drive creation or update workflows.

## 9. Centralize Exception Handling
* Define a global handler class annotated with `@ControllerAdvice` (or `@RestControllerAdvice` for REST APIs) using `@ExceptionHandler` methods to handle specific exceptions.
* Return consistent error responses. Consider using the ProblemDetails response format ([RFC 9457](https://www.rfc-editor.org/rfc/rfc9457)).

## 10. Actuator
* Expose only essential actuator endpoints (such as `/health`, `/info`, `/metrics`) without requiring authentication. All the other actuator endpoints must be secured.

## 11. Internationalization with ResourceBundles
* Externalize all user-facing text such as labels, prompts, and messages into ResourceBundles rather than embedding them in code.

## 12. Use Testcontainers for integration tests
* Spin up real services (databases, message brokers, etc.) in your integration tests to mirror production environments.

## 13. Use random port for integration tests
* When writing integration tests, start the application on a random available port to avoid port conflicts by annotating the test class with:

    ```java
    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    ```

## 14. Logging
* **Use a proper logging framework.**  
  Never use `System.out.println()` for application logging. Rely on SLF4J (or a compatible abstraction) and your chosen backend (Logback, Log4j2, etc.).

* **Protect sensitive data.**  
  Ensure that no credentials, personal information, or other confidential details ever appear in log output.

* **Guard expensive log calls.**  
  When building verbose messages at `DEBUG` or `TRACE` level, especially those involving method calls or complex string concatenations, wrap them in a level check or use suppliers:

```java
if (logger.isDebugEnabled()) {
    logger.debug("Detailed state: {}", computeExpensiveDetails());
}

// using Supplier/Lambda expression
logger.atDebug()
	.setMessage("Detailed state: {}")
	.addArgument(() -> computeExpensiveDetails())
    .log();
```