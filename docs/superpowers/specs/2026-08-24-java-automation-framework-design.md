# Java Automation Framework Design

## Goal

Create a maintainable Java 17 Maven project that separates reusable automation setup, Selenium UI tests, and REST Assured API tests. The project must be easy to open in IntelliJ IDEA and support local and CI execution.

## Module Layout

```text
selenium-test-framework/
├── pom.xml
├── automation-framework/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/carrie188/automation/
│       │   ├── api/
│       │   ├── config/
│       │   ├── driver/
│       │   ├── reporting/
│       │   ├── ui/
│       │   └── utils/
│       └── resources/
├── automation-ui-tests/
│   ├── pom.xml
│   └── src/test/
│       ├── java/com/carrie188/automation/ui/
│       │   ├── pages/
│       │   └── tests/
│       └── resources/
└── automation-api-tests/
    ├── pom.xml
    └── src/test/
        ├── java/com/carrie188/automation/api/
        │   ├── clients/
        │   └── tests/
        └── resources/
```

The root `pom.xml` is an aggregator and parent POM. It defines Java version, dependency versions, Maven plugins, and the three child modules. Each child module has its own POM and inherits the shared configuration.

## Responsibilities

### automation-framework

Contains reusable production code only.

- `config`: reads test settings from system properties with safe local defaults.
- `driver`: creates and closes Chrome WebDriver instances; headless mode is configurable.
- `ui`: base page and explicit-wait helpers for page objects.
- `api`: shared REST Assured request and response specifications.
- `reporting`: concise request, response, and test logging without secrets.
- `utils`: small framework helpers with a single clear purpose.

### automation-ui-tests

Contains product-specific Selenium tests.

- `pages`: page objects that represent browser screens and actions.
- `tests`: JUnit 5 UI tests that use page objects and the shared driver.
- Includes a smoke-test example against a public stable page, clearly labelled as an example that can be replaced with the real application URL.

### automation-api-tests

Contains product-specific REST Assured tests.

- `clients`: endpoint wrappers that keep request construction out of tests.
- `tests`: JUnit 5 API tests that assert status codes and response content.
- Includes a health-check example against a public endpoint, clearly labelled as replaceable.

## Test Execution

`mvn test` runs the framework build and both test modules.

```bash
mvn test
mvn -pl automation-ui-tests test
mvn -pl automation-api-tests test
```

System properties select runtime settings without code changes:

```bash
mvn test -Dbrowser=chrome -Dheadless=true \
  -Dbase.url=https://example.test \
  -Dapi.base.url=https://api.example.test
```

UI and API tests are tagged so CI can run focused smoke, UI, or API jobs later. The initial project keeps Maven configuration straightforward and does not embed CI-provider-specific files.

## Quality and Failure Handling

- JUnit 5 owns test lifecycle and assertions.
- Selenium uses explicit waits; there are no arbitrary sleep calls.
- Browser teardown runs even after a failed assertion.
- REST Assured logs request and response details on failures, excluding sensitive headers.
- Configuration values can be overridden through Maven system properties.
- The project includes a README with IntelliJ import and command-line instructions.

## Verification

The scaffold will be verified with Maven compilation and targeted API/UI test execution where the runtime permits browser-driver execution. The project will also include minimal tests for framework configuration defaults before implementation code is added.
