# ExpenseTracker

ExpenseTracker is a Java desktop application for managing income and expenses.
It can use MySQL storage when database settings are configured, or in-memory
storage otherwise.

## Java requirement

Use **JDK 25 LTS** to build and run the project. The build targets Java 25;
Java 26 is not the target. Maven 3.9 or later is recommended.

Run the automated tests from the project root:

```sh
mvn clean test
```

## Project layout

- `src/main/java/com/team/expense/` — application entry point, transaction
  model, DAO, service, Swing UI, and database connection utilities
- `src/main/resources/schema.sql` — MySQL schema
- `src/test/java/com/team/expense/` — JUnit tests
- `pom.xml` — Java release target and Maven dependencies/plugins
