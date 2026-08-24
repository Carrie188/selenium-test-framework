# Java Selenium + REST Assured + JUnit Starter

A ready-to-run Maven test starter using Selenium WebDriver, REST Assured, and JUnit 5.

## Prerequisites

- JDK 17+
- Maven 3.9+
- Chrome installed (Selenium Manager resolves a compatible driver)

## Run

```bash
mvn test
```

Override targets:

```bash
mvn test -Dbase.url=https://www.selenium.dev/ -Dapi.base.url=https://httpbin.org -Dheadless=true
```

The starter includes:

- a Selenium browser smoke test
- a REST Assured API health check
- automatic browser shutdown after each test
