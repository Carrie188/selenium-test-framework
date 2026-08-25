# Java Automation Framework Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Java 17 Maven multi-module automation project that shares framework code while keeping Selenium UI and REST Assured API test cases independent.

**Architecture:** A root Maven aggregator owns dependency and plugin versions. `automation-framework` is a reusable library for settings, browser lifecycle, UI waits, and REST Assured specifications. `automation-ui-tests` and `automation-api-tests` depend on that library and own product-specific page objects, API clients, and JUnit 5 test cases.

**Tech Stack:** Java 17, Maven, JUnit Jupiter 5.11.4, Selenium 4.27.0, REST Assured 5.5.0, Maven Surefire 3.5.2, SLF4J Simple 2.0.16.

**Spec:** `docs/superpowers/specs/2026-08-24-java-automation-framework-design.md`

## Global Constraints

- Java 17 is the project compiler release.
- Maven owns shared versions through the root parent/aggregator POM; every child module has its own POM.
- Use JUnit 5 lifecycle and assertions.
- Use explicit Selenium waits; do not add `Thread.sleep`.
- Always close the browser after each UI test.
- Read browser, headless, UI URL, and API URL from Maven system properties with non-empty defaults.
- Log REST Assured traffic on validation failure and blacklist `Authorization` and `Cookie` headers.
- Keep test code separate: UI cases only in `automation-ui-tests`; API cases only in `automation-api-tests`.
- Document IntelliJ import and module-specific Maven commands in `README.md`.

---

## File Structure

- `pom.xml` — root Maven aggregator, common properties, dependency management, plugin management, and module list.
- `automation-framework/pom.xml` — shared Java library dependencies.
- `automation-ui-tests/pom.xml` — UI test module with Selenium, JUnit, and the shared library.
- `automation-api-tests/pom.xml` — API test module with REST Assured, JUnit, and the shared library.
- `automation-framework/src/main/java/com/carrie188/automation/config/TestSettings.java` — immutable runtime settings parsed from system properties.
- `automation-framework/src/main/java/com/carrie188/automation/config/Browser.java` — supported browser values.
- `automation-framework/src/main/java/com/carrie188/automation/driver/DriverFactory.java` — WebDriver construction and browser options.
- `automation-framework/src/main/java/com/carrie188/automation/ui/BasePage.java` — explicit-wait page object base class.
- `automation-framework/src/main/java/com/carrie188/automation/api/ApiSpecifications.java` — shared REST Assured request specification.
- `automation-framework/src/test/java/com/carrie188/automation/config/TestSettingsTest.java` — unit tests for default and overridden runtime settings.
- `automation-ui-tests/src/test/java/com/carrie188/automation/ui/pages/SeleniumHomePage.java` — example page object.
- `automation-ui-tests/src/test/java/com/carrie188/automation/ui/tests/BaseUiTest.java` — JUnit browser lifecycle.
- `automation-ui-tests/src/test/java/com/carrie188/automation/ui/tests/SeleniumHomePageTest.java` — tagged UI smoke test.
- `automation-api-tests/src/test/java/com/carrie188/automation/api/clients/HttpBinClient.java` — example API client.
- `automation-api-tests/src/test/java/com/carrie188/automation/api/tests/HttpBinHealthCheckTest.java` — tagged API smoke test.
- `README.md` — installation, IntelliJ import, configuration, and commands.

## Task 1: Maven Reactor and Module Contracts

**Files:**
- Create: `pom.xml`
- Create: `automation-framework/pom.xml`
- Create: `automation-ui-tests/pom.xml`
- Create: `automation-api-tests/pom.xml`
- Create: `.gitignore`

**Interfaces:**
- Produces: Maven coordinates `com.carrie188:automation-framework`, `com.carrie188:automation-ui-tests`, and `com.carrie188:automation-api-tests`, all version `1.0.0-SNAPSHOT`.
- Produces: root properties `base.url`, `api.base.url`, `browser`, and `headless` so Surefire never overwrites Java defaults with empty strings.

- [ ] **Step 1: Write the root POM with the reactor structure**

```xml
<packaging>pom</packaging>
<modules>
  <module>automation-framework</module>
  <module>automation-ui-tests</module>
  <module>automation-api-tests</module>
</modules>
<properties>
  <maven.compiler.release>17</maven.compiler.release>
  <base.url>https://www.selenium.dev/</base.url>
  <api.base.url>https://httpbin.org</api.base.url>
  <browser>chrome</browser>
  <headless>true</headless>
</properties>
```

- [ ] **Step 2: Write one child POM per module**

Use a shared parent section in every child POM:

```xml
<parent>
  <groupId>com.carrie188</groupId>
  <artifactId>selenium-test-framework</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</parent>
```

The framework POM declares Selenium, REST Assured, and SLF4J Simple. The two test POMs declare JUnit Jupiter and depend on `automation-framework` at `${project.version}`. Configure Surefire only in test modules with the four non-empty root properties:

```xml
<systemPropertyVariables>
  <base.url>${base.url}</base.url>
  <api.base.url>${api.base.url}</api.base.url>
  <browser>${browser}</browser>
  <headless>${headless}</headless>
</systemPropertyVariables>
```

- [ ] **Step 3: Add a `.gitignore`**

```gitignore
target/
.idea/
*.iml
*.log
```

- [ ] **Step 4: Verify the empty reactor compiles**

Run: `mvn -Dmaven.repo.local=/private/tmp/selenium-framework-m2 test -DskipTests`

Expected: Maven reports `BUILD SUCCESS` and lists all three modules.

- [ ] **Step 5: Record the repository state**

Run: `git status --short 2>/dev/null || true`

Expected: no commit is attempted because the target folder is not yet a Git repository.

## Task 2: Runtime Configuration with Test-First Defaults

**Files:**
- Create: `automation-framework/src/test/java/com/carrie188/automation/config/TestSettingsTest.java`
- Create: `automation-framework/src/main/java/com/carrie188/automation/config/Browser.java`
- Create: `automation-framework/src/main/java/com/carrie188/automation/config/TestSettings.java`

**Interfaces:**
- Produces: `TestSettings.fromSystemProperties(): TestSettings`.
- Produces: `Browser.from(String value): Browser`.
- Consumes: system properties `base.url`, `api.base.url`, `browser`, and `headless`.

- [ ] **Step 1: Write the failing configuration tests**

```java
@Test
void usesStableDefaultsWhenPropertiesAreAbsent() {
    TestSettings settings = TestSettings.fromSystemProperties();
    assertEquals("https://www.selenium.dev/", settings.baseUrl());
    assertEquals("https://httpbin.org", settings.apiBaseUrl());
    assertEquals(Browser.CHROME, settings.browser());
    assertTrue(settings.headless());
}

@Test
void readsExplicitRuntimeOverrides() {
    System.setProperty("browser", "firefox");
    System.setProperty("headless", "false");
    System.setProperty("base.url", "https://ui.example.test");
    System.setProperty("api.base.url", "https://api.example.test");
    TestSettings settings = TestSettings.fromSystemProperties();
    assertEquals(Browser.FIREFOX, settings.browser());
    assertFalse(settings.headless());
    assertEquals("https://ui.example.test", settings.baseUrl());
    assertEquals("https://api.example.test", settings.apiBaseUrl());
}
```

Use `@AfterEach` to clear these four properties so the tests never leak configuration.

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn -pl automation-framework test -Dtest=TestSettingsTest`

Expected: test compilation fails because `TestSettings` and `Browser` do not exist.

- [ ] **Step 3: Implement the minimum settings API**

```java
public enum Browser {
    CHROME, FIREFOX;

    public static Browser from(String value) {
        return Browser.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}

public record TestSettings(String baseUrl, String apiBaseUrl, Browser browser, boolean headless) {
    public static TestSettings fromSystemProperties() {
        return new TestSettings(
            System.getProperty("base.url", "https://www.selenium.dev/"),
            System.getProperty("api.base.url", "https://httpbin.org"),
            Browser.from(System.getProperty("browser", "chrome")),
            Boolean.parseBoolean(System.getProperty("headless", "true"))
        );
    }
}
```

- [ ] **Step 4: Run the configuration tests again**

Run: `mvn -pl automation-framework test -Dtest=TestSettingsTest`

Expected: `Tests run: 2, Failures: 0, Errors: 0`.

- [ ] **Step 5: Verify the entire framework module**

Run: `mvn -pl automation-framework test`

Expected: `BUILD SUCCESS`.

## Task 3: Shared Browser and API Support

**Files:**
- Create: `automation-framework/src/main/java/com/carrie188/automation/driver/DriverFactory.java`
- Create: `automation-framework/src/main/java/com/carrie188/automation/ui/BasePage.java`
- Create: `automation-framework/src/main/java/com/carrie188/automation/api/ApiSpecifications.java`

**Interfaces:**
- Consumes: `TestSettings` and `Browser` from Task 2.
- Produces: `DriverFactory.create(TestSettings): WebDriver`.
- Produces: `BasePage(WebDriver driver)` with `waitUntilVisible(By locator): WebElement`.
- Produces: `ApiSpecifications.request(TestSettings): RequestSpecification`.

- [ ] **Step 1: Write a failing API-specification test**

Add this test to `TestSettingsTest.java`:

```java
@Test
void buildsRequestSpecificationFromApiBaseUrl() {
    RequestSpecification specification = ApiSpecifications.request(
        new TestSettings("https://ui.example.test", "https://api.example.test", Browser.CHROME, true)
    );
    assertEquals("https://api.example.test", specification.getBaseUri());
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn -pl automation-framework test -Dtest=TestSettingsTest`

Expected: compilation fails because `ApiSpecifications` does not exist.

- [ ] **Step 3: Implement the shared adapters**

```java
public static RequestSpecification request(TestSettings settings) {
    return new RequestSpecBuilder()
        .setBaseUri(settings.apiBaseUrl())
        .setConfig(RestAssured.config().logConfig(
            LogConfig.logConfig().blacklistHeader("Authorization").blacklistHeader("Cookie")))
        .build();
}
```

`DriverFactory.create` uses `ChromeOptions` for Chrome and `FirefoxOptions` for Firefox, adds `--headless=new` only when `settings.headless()` is true, and throws `IllegalArgumentException` for an unsupported enum value. `BasePage` creates `new WebDriverWait(driver, Duration.ofSeconds(10))` and performs `wait.until(ExpectedConditions.visibilityOfElementLocated(locator))`.

- [ ] **Step 4: Run the shared-framework tests**

Run: `mvn -pl automation-framework test`

Expected: configuration and API-specification tests pass.

- [ ] **Step 5: Compile the reactor**

Run: `mvn -Dmaven.repo.local=/private/tmp/selenium-framework-m2 test -DskipTests`

Expected: all modules compile after adding shared support.

## Task 4: Selenium UI Module and Page Object Example

**Files:**
- Create: `automation-ui-tests/src/test/java/com/carrie188/automation/ui/pages/SeleniumHomePage.java`
- Create: `automation-ui-tests/src/test/java/com/carrie188/automation/ui/tests/BaseUiTest.java`
- Create: `automation-ui-tests/src/test/java/com/carrie188/automation/ui/tests/SeleniumHomePageTest.java`

**Interfaces:**
- Consumes: `DriverFactory.create(TestSettings)`, `TestSettings.fromSystemProperties()`, and `BasePage`.
- Produces: `SeleniumHomePage.open(String baseUrl): SeleniumHomePage` and `pageTitle(): String`.
- Produces: `BaseUiTest` browser setup and guaranteed teardown.

- [ ] **Step 1: Write the UI test before the page object**

```java
@Tag("ui")
@Tag("smoke")
class SeleniumHomePageTest extends BaseUiTest {
    @Test
    void displaysTheSeleniumHomePageTitle() {
        SeleniumHomePage homePage = new SeleniumHomePage(driver).open(settings.baseUrl());
        assertTrue(homePage.pageTitle().contains("Selenium"));
    }
}
```

- [ ] **Step 2: Run the test to verify it fails at compilation**

Run: `mvn -pl automation-ui-tests test -Dtest=SeleniumHomePageTest`

Expected: compilation fails because `BaseUiTest` and `SeleniumHomePage` do not exist.

- [ ] **Step 3: Implement browser lifecycle and the page object**

```java
public abstract class BaseUiTest {
    protected WebDriver driver;
    protected TestSettings settings;

    @BeforeEach
    void startBrowser() {
        settings = TestSettings.fromSystemProperties();
        driver = DriverFactory.create(settings);
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

`SeleniumHomePage.open` calls `driver.get(baseUrl)` and returns `this`; `pageTitle` returns `driver.getTitle()`. Do not use a static WebDriver or implicit waits.

- [ ] **Step 4: Run the UI test headlessly**

Run: `mvn -pl automation-ui-tests test -Dbrowser=chrome -Dheadless=true`

Expected: the Selenium smoke test passes where Selenium Manager can execute a browser driver. If the Codex sandbox blocks driver execution, record that as an environment limitation and retain compilation evidence.

- [ ] **Step 5: Compile the UI module without a browser**

Run: `mvn -pl automation-ui-tests test -DskipTests`

Expected: `BUILD SUCCESS`.

## Task 5: REST Assured API Module and Example Client

**Files:**
- Create: `automation-api-tests/src/test/java/com/carrie188/automation/api/clients/HttpBinClient.java`
- Create: `automation-api-tests/src/test/java/com/carrie188/automation/api/tests/HttpBinHealthCheckTest.java`

**Interfaces:**
- Consumes: `ApiSpecifications.request(TestSettings)` and `TestSettings.fromSystemProperties()`.
- Produces: `HttpBinClient.get(): Response`.

- [ ] **Step 1: Write the API health-check test first**

```java
@Tag("api")
@Tag("smoke")
class HttpBinHealthCheckTest {
    @Test
    void getEndpointReturnsOk() {
        Response response = new HttpBinClient(TestSettings.fromSystemProperties()).get();
        assertEquals(200, response.statusCode());
    }
}
```

- [ ] **Step 2: Run the API test to verify it fails at compilation**

Run: `mvn -pl automation-api-tests test -Dtest=HttpBinHealthCheckTest`

Expected: compilation fails because `HttpBinClient` does not exist.

- [ ] **Step 3: Implement the smallest API client**

```java
public final class HttpBinClient {
    private final RequestSpecification request;

    public HttpBinClient(TestSettings settings) {
        request = ApiSpecifications.request(settings);
    }

    public Response get() {
        return given().spec(request).when().get("/get");
    }
}
```

- [ ] **Step 4: Run the API test with the public example endpoint**

Run: `mvn -pl automation-api-tests test -Dapi.base.url=https://httpbin.org`

Expected: `Tests run: 1, Failures: 0, Errors: 0`.

- [ ] **Step 5: Run API tests by tag**

Run: `mvn -pl automation-api-tests test -Dgroups=api -Dapi.base.url=https://httpbin.org`

Expected: only tests tagged `api` run and pass.

## Task 6: Documentation and Final Verification

**Files:**
- Create: `README.md`

**Interfaces:**
- Documents: modules and commands from Tasks 1–5.

- [ ] **Step 1: Write the README with exact IntelliJ import instructions**

Include these instructions:

```text
1. In IntelliJ IDEA, choose Open.
2. Select the root folder containing the parent pom.xml.
3. Trust the project and allow Maven import to finish.
4. Set Project SDK to JDK 17 or newer.
```

Also document the root, UI-only, API-only, headless, and visible-browser commands from the spec.

- [ ] **Step 2: Verify the Maven build without browser execution**

Run: `mvn -Dmaven.repo.local=/private/tmp/selenium-framework-m2 test -DskipTests`

Expected: every reactor module compiles and Maven reports `BUILD SUCCESS`.

- [ ] **Step 3: Verify the API test end to end**

Run: `mvn -Dmaven.repo.local=/private/tmp/selenium-framework-m2 -pl automation-api-tests test -Dapi.base.url=https://httpbin.org`

Expected: the API health-check test passes.

- [ ] **Step 4: Run the full configured suite**

Run: `mvn -Dmaven.repo.local=/private/tmp/selenium-framework-m2 test -Dbrowser=chrome -Dheadless=true -Dbase.url=https://www.selenium.dev/ -Dapi.base.url=https://httpbin.org`

Expected: report the exact result. If browser-driver execution is sandbox-blocked, state that the API test passed and UI compilation succeeded, and provide the command for a normal local terminal.

- [ ] **Step 5: Review the final structure**

Run: `find . -path './target' -prune -o -type f -print | sort`

Expected: source files appear only in their designated module and no generated `target` files are included in the source structure.
