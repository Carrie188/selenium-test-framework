# Selenium Test Framework

A Java 17, Maven multi-module test automation starter that separates reusable framework code from Selenium UI and REST Assured API tests.

## Modules

```text
selenium-test-framework/
├── automation-framework/   # Shared configuration, WebDriver, page, and REST Assured support
├── automation-ui-tests/    # Selenium page objects and UI test cases
├── automation-api-tests/   # REST Assured clients and API test cases
└── pom.xml                 # Parent POM and Maven reactor
```

The shared `automation-framework` module contains production code only. Product-specific tests remain in their dedicated UI and API modules, keeping the project ready to grow without mixing concerns.

## Prerequisites

- JDK 17
- Maven 3.9 or newer
- Google Chrome for the default UI example (or Firefox with `-Dbrowser=firefox`)
- IntelliJ IDEA Community or Ultimate

Selenium Manager obtains a compatible driver when the UI tests run.

## Run locally

From the repository root:

```bash
mvn test
```

Run one test area:

```bash
mvn -pl automation-ui-tests test
mvn -pl automation-api-tests test
```

The included samples use `selenium.dev` for the UI smoke test and `httpbin.org` for the API smoke test. Replace the example page object and API client with your product's own tests.

## Runtime configuration

Configuration is supplied with Maven system properties, so CI and local runs do not require code changes.

```bash
mvn test \
  -Dbrowser=chrome \
  -Dheadless=true \
  -Dbase.url=https://your-app.example \
  -Dapi.base.url=https://api.your-app.example
```

| Property | Default | Purpose |
| --- | --- | --- |
| `browser` | `chrome` | Browser for UI tests (`chrome` or `firefox`) |
| `headless` | `true` | Runs the browser without a visible window when `true` |
| `base.url` | `https://www.selenium.dev/` | Base address used by UI tests |
| `api.base.url` | `https://httpbin.org` | Base address used by API tests |

## IntelliJ IDEA

1. Clone this repository locally.
2. Select **File → Open** and choose the repository's root `pom.xml`.
3. Trust the project and let IntelliJ import the Maven reactor.
4. Set **Project SDK** to JDK 17 if IntelliJ does not detect it automatically.
5. Run a test class from either test module, or use the Maven tool window to run `test` from the root module.

## Test design conventions

- Keep reusable Selenium, configuration, and REST Assured setup in `automation-framework`.
- Keep page objects and UI scenarios in `automation-ui-tests`.
- Keep endpoint clients and API scenarios in `automation-api-tests`.
- Use JUnit tags (`ui`, `api`, and `smoke`) for future CI selection.
- Use explicit waits in page objects; avoid `Thread.sleep`.
- The shared REST Assured setup blacklists `Authorization` and `Cookie` headers in logging.

## Documentation

The approved architecture and implementation plan are included in `docs/superpowers/`.
