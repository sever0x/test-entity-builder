package io.github.sever0x.testentitybuilder.generation;

import io.github.sever0x.testentitybuilder.annotation.GenerateBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Static Nested Classes Tests")
class StaticNestedClassesTest {

    public static class OuterClass {
        @GenerateBuilder
        public static class NestedEntity {
            private String name;
            private int value;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public int getValue() { return value; }
            public void setValue(int value) { this.value = value; }
        }

        @GenerateBuilder
        public static class AnotherNestedEntity {
            private NestedEntity nestedEntity;
            private String description;

            public NestedEntity getNestedEntity() { return nestedEntity; }
            public void setNestedEntity(NestedEntity nestedEntity) { this.nestedEntity = nestedEntity; }
            public String getDescription() { return description; }
            public void setDescription(String description) { this.description = description; }
        }
    }

    @Test
    @DisplayName("Should correctly build static nested class instance")
    void shouldBuildNestedClassInstance() {
        // When
        OuterClass.NestedEntity entity = new NestedEntityBuilder()
                .withName("test")
                .withValue(42)
                .build();

        // Then
        assertThat(entity.getName()).isEqualTo("test");
        assertThat(entity.getValue()).isEqualTo(42);
    }

    @Test
    @DisplayName("Should support nested classes referencing other nested classes")
    void shouldSupportNestedClassesReferences() {
        // Given
        OuterClass.NestedEntity nested = new NestedEntityBuilder()
                .withName("inner")
                .withValue(1)
                .build();

        // When
        OuterClass.AnotherNestedEntity entity = new AnotherNestedEntityBuilder()
                .withNestedEntity(nested)
                .withDescription("test")
                .build();

        // Then
        assertThat(entity.getDescription()).isEqualTo("test");
        assertThat(entity.getNestedEntity())
                .isNotNull()
                .satisfies(ne -> {
                    assertThat(ne.getName()).isEqualTo("inner");
                    assertThat(ne.getValue()).isEqualTo(1);
                });
    }
}