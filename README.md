# Test Entity Builder

A Java library that generates builders for entities, making it easier to create test data.

## How to Use

1. Add the annotation to your class:

```java
@GenerateBuilder
public class Person {
    private String firstName;
    private String lastName;
    private int age;
    private boolean active;

    // getters and setters
}
```

2. Use the generated builder:

```java
// Using generated builder
Person person = new PersonBuilder()
    .withFirstName("John")
    .withLastName("Doe")
    .withAge(25)
    .withActive(true)
    .build();

// Or using generic builder
Person person = EntityBuilder.of(Person.class)
    .with("firstName", "John")
    .with("lastName", "Doe")
    .with("age", 25)
    .with("active", true)
    .build();
```

## Features

- Automatic builder generation with `@GenerateBuilder` annotation
- Support for primitive types and their wrappers
- Collection support (List, Set, Map)
- LocalDate, LocalDateTime, and BigDecimal support
- Enum support
- Inheritance support
- Logging with SLF4J + Logback