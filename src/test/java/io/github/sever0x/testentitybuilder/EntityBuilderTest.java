package io.github.sever0x.testentitybuilder;

import io.github.sever0x.EntityBuilder;
import io.github.sever0x.exception.FieldAccessException;
import io.github.sever0x.exception.ObjectCreationException;
import io.github.sever0x.testentitybuilder.model.Person;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EntityBuilderTest {

    @Test
    void shouldCreateEntityWithDefaultValues() {
        Person person = EntityBuilder.of(Person.class).build();

        assertThat(person).isNotNull();
        assertThat(person.getFirstName()).isNotNull().isNotEmpty();
        assertThat(person.getLastName()).isNotNull().isNotEmpty();
        assertThat(person.getAge()).isPositive();
        assertThat(person.isActive()).isFalse();
    }

    @Test
    void shouldCreateEntityWithCustomValues() {
        Person person = EntityBuilder.of(Person.class)
                .with("firstName", "John")
                .with("lastName", "Doe")
                .with("age", 25)
                .with("active", true)
                .build();

        assertThat(person.getFirstName()).isEqualTo("John");
        assertThat(person.getLastName()).isEqualTo("Doe");
        assertThat(person.getAge()).isEqualTo(25);
        assertThat(person.isActive()).isTrue();
    }

    @Test
    void shouldThrowExceptionForPrivateConstructor() {
        class PrivateConstructorClass {
            private PrivateConstructorClass() {}
        }
        ObjectCreationException exception = assertThrows(ObjectCreationException.class, () -> EntityBuilder.of(PrivateConstructorClass.class).build());
        assertThat(exception)
                .hasMessageContaining("no-args constructor")
                .hasMessageContaining(PrivateConstructorClass.class.getName());
    }

    @Test
    void shouldThrowExceptionForIncompatibleType() {
        FieldAccessException exception = assertThrows(
                FieldAccessException.class,
                () -> EntityBuilder.of(Person.class)
                        .with("age", "not a number")
                        .build()
        );

        assertThat(exception)
                .hasMessageContaining("age")
                .hasMessageContaining("String")
                .hasMessageContaining("int");
    }

    @Test
    void shouldThrowExceptionForNonExistentField() {
        FieldAccessException exception = assertThrows(
                FieldAccessException.class,
                () -> EntityBuilder.of(Person.class)
                        .with("nonExistentField", "value")
                        .build()
        );

        assertThat(exception)
                .hasMessageContaining("nonExistentField")
                .hasMessageContaining("doesn't exist");
    }
}
