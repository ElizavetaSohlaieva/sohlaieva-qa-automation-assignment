# QA Automation Test Suite

## Prerequisites
- Java 11+
- Maven 3.6+
- Chrome browser

## How to Run
```bash
# Run all tests
mvn clean test

# Run only API tests
mvn test -Dgroups="api"

# Run only UI tests
mvn test -Dgroups="ui"

##Test Strategy
API tests were implemented using typed request/response models, shared specifications, and clear separation of positive and negative scenarios across REST and GraphQL endpoints.
UI tests were built with Playwright using Page Object Model and a Chain‑of‑Responsibility–style fluent API to structure actions and keep scenarios linear and predictable.

##Challenges & Solutions
Firstly I created tests with hardcorded data which did them unstable when date might suddenly be deleted or changed, but than decided to cteate new user before each test.
Some selectors were unstable.
I used more reliable locators used getByRole or getByPlayceholder Playwright locators.

##What I Would Add With More Time
Expanded Parameterized Coverage: Migrate remaining negative/validation tests to JUnit 5 @ParameterizedTest to run boundary values (prices, invalid dates) through single methods.
Parallel Execution Optimization: Configure Maven Surefire to run API and UI suites concurrently to cut down CI execution time.
Secrets Management Integration: Extract hardcoded credentials (USERNAME/PASSWORD) from the code and inject them via GitHub Repository Secrets and environment variables.