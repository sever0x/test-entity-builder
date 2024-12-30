package io.github.sever0x.testentitybuilder.generation;

import io.github.sever0x.testentitybuilder.generation.model.ExtendedTypesEntity;
import io.github.sever0x.testentitybuilder.generation.model.ExtendedTypesEntityBuilder;
import io.github.sever0x.testentitybuilder.generation.model.TestEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Extended Types Builder Tests")
class ExtendedTypesTest {

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTest {

        @Test
        @DisplayName("Should create instance with all default values")
        void shouldCreateInstanceWithDefaults() {
            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder().build();

            // Then
            assertThat(entity).isNotNull();
        }
    }

    @Nested
    @DisplayName("Primitive Types Tests")
    class PrimitiveTypesTest {

        @Test
        @DisplayName("Should set default values for primitive types")
        void shouldSetDefaultValuesForPrimitives() {
            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder().build();

            // Then
            assertThat(entity.getByteValue()).isEqualTo((byte) 1);
            assertThat(entity.getShortValue()).isEqualTo((short) 1);
            assertThat(entity.getFloatValue()).isEqualTo(1.0f);
            assertThat(entity.getDoubleValue()).isEqualTo(1.0d);
            assertThat(entity.getCharValue()).isEqualTo('A');
        }

        @Test
        @DisplayName("Should correctly set custom primitive values")
        void shouldSetCustomPrimitiveValues() {
            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder()
                    .withByteValue((byte) 42)
                    .withShortValue((short) 123)
                    .withFloatValue(3.14f)
                    .withDoubleValue(2.718)
                    .withCharValue('Z')
                    .build();

            // Then
            assertThat(entity.getByteValue()).isEqualTo((byte) 42);
            assertThat(entity.getShortValue()).isEqualTo((short) 123);
            assertThat(entity.getFloatValue()).isEqualTo(3.14f, within(0.0001f));
            assertThat(entity.getDoubleValue()).isEqualTo(2.718, within(0.0001));
            assertThat(entity.getCharValue()).isEqualTo('Z');
        }
    }

    @Nested
    @DisplayName("Wrapper Types Tests")
    class WrapperTypesTest {

        @Test
        @DisplayName("Should set default values for wrapper types")
        void shouldSetDefaultValuesForWrappers() {
            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder().build();

            // Then
            assertThat(entity.getByteWrapper()).isEqualTo((byte) 1);
            assertThat(entity.getShortWrapper()).isEqualTo((short) 1);
            assertThat(entity.getFloatWrapper()).isEqualTo(1.0f);
            assertThat(entity.getDoubleWrapper()).isEqualTo(1.0d);
            assertThat(entity.getCharWrapper()).isEqualTo('A');
        }

        @Test
        @DisplayName("Should handle null values for wrappers")
        void shouldHandleNullValues() {
            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder()
                    .withByteWrapper(null)
                    .withShortWrapper(null)
                    .withFloatWrapper(null)
                    .withDoubleWrapper(null)
                    .withCharWrapper(null)
                    .build();

            // Then
            assertThat(entity.getByteWrapper()).isNull();
            assertThat(entity.getShortWrapper()).isNull();
            assertThat(entity.getFloatWrapper()).isNull();
            assertThat(entity.getDoubleWrapper()).isNull();
            assertThat(entity.getCharWrapper()).isNull();
        }
    }

    @Nested
    @DisplayName("Collection Types Tests")
    class CollectionTypesTest {

        @Test
        @DisplayName("Should create empty collections by default")
        void shouldCreateEmptyCollections() {
            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder().build();

            // Then
            assertThat(entity.getStringList())
                    .isNotNull()
                    .isEmpty();
            assertThat(entity.getIntegerSet())
                    .isNotNull()
                    .isEmpty();
            assertThat(entity.getStringMap())
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("Should accept custom collections")
        void shouldAcceptCustomCollections() {
            // Given
            List<String> strings = Arrays.asList("one", "two", "three");
            Set<Integer> numbers = new HashSet<>(Arrays.asList(1, 2, 3));
            Map<String, String> map = new HashMap<String, String>() {{
                put("key1", "value1");
                put("key2", "value2");
            }};

            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder()
                    .withStringList(strings)
                    .withIntegerSet(numbers)
                    .withStringMap(map)
                    .build();

            // Then
            assertThat(entity.getStringList())
                    .containsExactly("one", "two", "three");
            assertThat(entity.getIntegerSet())
                    .containsExactlyInAnyOrder(1, 2, 3);
            assertThat(entity.getStringMap())
                    .containsEntry("key1", "value1")
                    .containsEntry("key2", "value2");
        }
    }

    @Nested
    @DisplayName("Special Types Tests")
    class SpecialTypesTest {

        @Test
        @DisplayName("Should set appropriate default values for special types")
        void shouldSetDefaultValuesForSpecialTypes() {
            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder().build();
            LocalDateTime now = LocalDateTime.now();

            // Then
            assertThat(entity.getDateValue()).isEqualTo(LocalDate.now());
            assertThat(entity.getDateTimeValue())
                    .isNotNull()
                    .isBetween(now.minusSeconds(1), now.plusSeconds(1));
            assertThat(entity.getBigDecimalValue()).isEqualTo(BigDecimal.ONE);
            Assertions.assertThat(entity.getEnumValue()).isEqualTo(TestEnum.FIRST);
        }

        @Test
        @DisplayName("Should accept custom special type values")
        void shouldAcceptCustomSpecialTypeValues() {
            // Given
            LocalDate date = LocalDate.of(2024, 1, 1);
            LocalDateTime dateTime = LocalDateTime.of(2024, 1, 1, 12, 0);
            BigDecimal decimal = new BigDecimal("123.456");

            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder()
                    .withDateValue(date)
                    .withDateTimeValue(dateTime)
                    .withBigDecimalValue(decimal)
                    .withEnumValue(TestEnum.SECOND)
                    .build();

            // Then
            assertThat(entity.getDateValue()).isEqualTo(date);
            assertThat(entity.getDateTimeValue()).isEqualTo(dateTime);
            assertThat(entity.getBigDecimalValue()).isEqualTo(decimal);
            Assertions.assertThat(entity.getEnumValue()).isEqualTo(TestEnum.SECOND);
        }
    }

    @Nested
    @DisplayName("Type Conversion Tests")
    class TypeConversionTest {

        private static Stream<Arguments> numericConversionTestData() {
            return Stream.of(
                    arguments(1, Byte.class),
                    arguments(1, Short.class),
                    arguments(1, Integer.class),
                    arguments(1L, Long.class),
                    arguments(1.0f, Float.class),
                    arguments(1.0d, Double.class)
            );
        }

        @ParameterizedTest(name = "Should convert {0} to {1}")
        @MethodSource("numericConversionTestData")
        @DisplayName("Should handle numeric type conversions")
        void shouldHandleNumericConversions(Number value) {
            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder()
                    .withByteValue((byte) value.intValue())
                    .withShortValue((short) value.intValue())
                    .withFloatValue(value.floatValue())
                    .withDoubleValue(value.doubleValue())
                    .build();

            // Then
            assertThat(entity).isNotNull();
        }
    }

    @Nested
    @DisplayName("Builder Chain Tests")
    class BuilderChainTest {

        @Test
        @DisplayName("Should support method chaining in any order")
        void shouldSupportMethodChaining() {
            // When
            ExtendedTypesEntity entity1 = new ExtendedTypesEntityBuilder()
                    .withByteValue((byte) 1)
                    .withShortValue((short) 2)
                    .withFloatValue(3.0f)
                    .build();

            ExtendedTypesEntity entity2 = new ExtendedTypesEntityBuilder()
                    .withFloatValue(3.0f)
                    .withByteValue((byte) 1)
                    .withShortValue((short) 2)
                    .build();

            // Then
            assertThat(entity1)
                    .usingRecursiveComparison()
                    .ignoringFields("dateValue", "dateTimeValue")
                    .isEqualTo(entity2);

            assertThat(entity1.getDateValue())
                    .isNotNull()
                    .isInstanceOf(LocalDate.class);
            assertThat(entity1.getDateTimeValue())
                    .isNotNull()
                    .isInstanceOf(LocalDateTime.class);
            assertThat(entity2.getDateValue())
                    .isNotNull()
                    .isInstanceOf(LocalDate.class);
            assertThat(entity2.getDateTimeValue())
                    .isNotNull()
                    .isInstanceOf(LocalDateTime.class);
        }

        @Test
        @DisplayName("Should override previous values in chain")
        void shouldOverridePreviousValues() {
            // When
            ExtendedTypesEntity entity = new ExtendedTypesEntityBuilder()
                    .withByteValue((byte) 1)
                    .withByteValue((byte) 2)
                    .withShortValue((short) 10)
                    .withShortValue((short) 20)
                    .build();

            // Then
            assertThat(entity.getByteValue()).isEqualTo((byte) 2);
            assertThat(entity.getShortValue()).isEqualTo((short) 20);
        }
    }
}