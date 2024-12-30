package io.github.sever0x.testentitybuilder.generation;

import io.github.sever0x.testentitybuilder.testmodel.Person;
import io.github.sever0x.testentitybuilder.testmodel.PersonBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Generated Builder Tests")
class GeneratedBuilderTest {

    @Nested
    @DisplayName("Default Values Handling")
    class DefaultValuesTests {
        @Test
        @DisplayName("Should create entity with all default values when no setters called")
        void shouldCreateEntityWithAllDefaultValues() {
            // When
            Person person = new PersonBuilder().build();

            // Then
            assertThat(person)
                    .satisfies(p -> {
                        assertThat(p.getFirstName()).isEqualTo("test_firstName");
                        assertThat(p.getLastName()).isEqualTo("test_lastName");
                        assertThat(p.getAge()).isEqualTo(1);
                        assertThat(p.isActive()).isFalse();
                    });
        }
    }

    @Nested
    @DisplayName("Partial Building Tests")
    class PartialBuildingTests {
        @Test
        @DisplayName("Should create entity with mix of custom and default values")
        void shouldCreateEntityWithMixedValues() {
            // When
            Person person = new PersonBuilder()
                    .withFirstName("John")
                    .withAge(30)
                    .build();

            // Then
            assertThat(person)
                    .satisfies(p -> {
                        assertThat(p.getFirstName()).isEqualTo("John");
                        assertThat(p.getLastName()).isEqualTo("test_lastName"); // default value
                        assertThat(p.getAge()).isEqualTo(30);
                        assertThat(p.isActive()).isFalse(); // default value
                    });
        }
    }

    @Nested
    @DisplayName("Method Chaining Tests")
    class MethodChainingTests {
        @Test
        @DisplayName("Should support different order of method calls")
        void shouldSupportDifferentMethodOrder() {
            // When
            Person person1 = new PersonBuilder()
                    .withFirstName("John")
                    .withLastName("Doe")
                    .withAge(25)
                    .withActive(true)
                    .build();

            Person person2 = new PersonBuilder()
                    .withAge(25)
                    .withActive(true)
                    .withLastName("Doe")
                    .withFirstName("John")
                    .build();

            // Then
            assertThat(person1)
                    .usingRecursiveComparison()
                    .isEqualTo(person2);
        }

        @Test
        @DisplayName("Should allow multiple calls to the same setter")
        void shouldAllowMultipleSetterCalls() {
            // When
            Person person = new PersonBuilder()
                    .withFirstName("John")
                    .withFirstName("Jane")
                    .build();

            // Then
            assertThat(person.getFirstName()).isEqualTo("Jane");
        }
    }

    @Nested
    @DisplayName("Null Handling Tests")
    class NullHandlingTests {
        @Test
        @DisplayName("Should accept null values for object fields")
        void shouldAcceptNullValues() {
            // When
            Person person = new PersonBuilder()
                    .withFirstName(null)
                    .withLastName(null)
                    .build();

            // Then
            assertThat(person)
                    .satisfies(p -> {
                        assertThat(p.getFirstName()).isNull();
                        assertThat(p.getLastName()).isNull();
                    });
        }
    }

    @Nested
    @DisplayName("Builder Reusability Tests")
    class BuilderReusabilityTests {
        @Test
        @DisplayName("Should create different instances with same builder")
        void shouldCreateDifferentInstances() {
            // Given
            PersonBuilder builder = new PersonBuilder()
                    .withFirstName("John")
                    .withLastName("Doe");

            // When
            Person person1 = builder.build();
            Person person2 = builder.build();

            // Then
            assertThat(person1)
                    .isNotSameAs(person2)
                    .usingRecursiveComparison()
                    .isEqualTo(person2);
        }
    }

    @Nested
    @DisplayName("Builder Creation Tests")
    class BuilderCreationTests {
        @Test
        @DisplayName("Should create builder instance without exceptions")
        void shouldCreateBuilderInstance() {
            assertThatCode(PersonBuilder::new)
                    .doesNotThrowAnyException();
        }
    }
}