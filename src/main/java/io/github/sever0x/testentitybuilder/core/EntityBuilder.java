package io.github.sever0x.testentitybuilder.core;

import io.github.sever0x.testentitybuilder.core.builder.AbstractEntityBuilder;
import io.github.sever0x.testentitybuilder.exception.FieldAccessException;
import io.github.sever0x.testentitybuilder.exception.ObjectCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * A flexible and type-safe builder implementation for creating objects with a fluent interface.
 * This class serves as the core building block for both runtime building and compile-time builder generation.
 *
 * <p>The builder supports:
 * <ul>
 *     <li>Setting field values using type-safe methods</li>
 *     <li>Automatic default value generation</li>
 *     <li>Inheritance handling</li>
 *     <li>Comprehensive error reporting</li>
 *     <li>Detailed logging of the building process</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>
 * {@code
 * Person person = EntityBuilder.of(Person.class)
 *     .with("firstName", "John")
 *     .with("age", 25)
 *     .build();
 * }
 * </pre>
 *
 * @param <T> The type of entity being built
 *
 * @see io.github.sever0x.testentitybuilder.annotation.GenerateBuilder
 * @see io.github.sever0x.testentitybuilder.core.builder.AbstractEntityBuilder
 */
public class EntityBuilder<T> {
    private static final Logger log = LoggerFactory.getLogger(EntityBuilder.class);
    private final String builderId = UUID.randomUUID().toString().substring(0, 8);

    private final Class<T> targetClass;
    private final Map<String, Object> customValues = new HashMap<>();

    private EntityBuilder(Class<T> targetClass) {
        this.targetClass = targetClass;
        log.debug("Creating EntityBuilder for class: {}", targetClass.getName());
    }

    /**
     * Creates a new EntityBuilder instance for the specified class.
     *
     * @param clazz The class of the entity to build
     * @param <T> The type of the entity
     * @return A new EntityBuilder instance
     * @throws IllegalArgumentException if the provided class is null
     */
    public static <T> EntityBuilder<T> of(Class<T> clazz) {
        if (clazz == null) {
            log.error("Attempted to create EntityBuilder with null class reference");
            throw new IllegalArgumentException("Target class can't be null");
        }
        return new EntityBuilder<>(clazz);
    }

    /**
     * Creates a typed builder instance for the specified class.
     * This method attempts to locate and instantiate a generated builder class.
     *
     * @param clazz The class to create a builder for
     * @param <T> The type of the entity
     * @return A new instance of the generated builder
     * @throws RuntimeException if the builder class cannot be found or instantiated
     */
    @SuppressWarnings("unchecked")
    public static <T> AbstractEntityBuilder<T, ?> builder(Class<T> clazz) {
        try {
            String builderClassName = clazz.getName() + "Builder";
            Class<?> builderClass = Class.forName(builderClassName);

            return (AbstractEntityBuilder<T, ?>) builderClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not create builder for " + clazz.getName(), e);
        }
    }

    /**
     * Sets a value for the specified field.
     *
     * @param fieldName The name of the field to set
     * @param value The value to set for the field
     * @return This builder instance for method chaining
     * @throws FieldAccessException if the field doesn't exist or the value type is incompatible
     */
    public EntityBuilder<T> with(String fieldName, Object value) {
        log.trace("EntityBuilder[id={}] setting field '{}' to value: {}", builderId, fieldName, value);

        Field field = getAllFields(targetClass).stream()
                .filter(f -> f.getName().equals(fieldName))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("EntityBuilder[id={}] field '{}' not found in class {}",
                            builderId, fieldName, targetClass.getName());
                    return new FieldAccessException(fieldName, targetClass, "Field doesn't exist");
                });

        validateFieldValue(field, value);
        customValues.put(fieldName, value);
        return this;
    }

    /**
     * Builds and returns an instance of the entity with all configured values.
     *
     * @return A new instance of the entity
     * @throws ObjectCreationException if the entity cannot be instantiated
     * @throws FieldAccessException if there are problems setting field values
     */
    public T build() {
        log.info("EntityBuilder[id={}] building instance of {} with {} custom values", builderId, targetClass.getName(), customValues.size());

        try {
            Constructor<T> constructor = targetClass.getDeclaredConstructor();
            if (!Modifier.isPublic(constructor.getModifiers())) {
                log.error("EntityBuilder[id={}] no public no-args constructor available for {}", builderId, targetClass.getName());
                throw new ObjectCreationException(targetClass, "No public no-args constructor available");
            }

            T instance = constructor.newInstance();

            log.debug("EntityBuilder[id={}] setting default values for {}", builderId, targetClass.getName());
            setDefaultValues(instance);

            log.debug("EntityBuilder[id={}] applying {} custom values to {}", builderId, customValues.size(), targetClass.getName());
            applyCustomValues(instance);

            log.debug("EntityBuilder[id={}] successfully built instance of {}", builderId, targetClass.getName());
            return instance;
        } catch (NoSuchMethodException e) {
            log.error("EntityBuilder[id={}] failed to find constructor for {}: {}", builderId, targetClass.getName(), e.getMessage());
            throw new ObjectCreationException(targetClass, "Class must have a no-args constructor");
        } catch (Exception e) {
            log.error("EntityBuilder[id={}] unexpected error building {}: {}", builderId, targetClass.getName(), e.getMessage(), e);
            throw new ObjectCreationException(targetClass, "Unexpected error: " + e.getMessage());
        }
    }

    private void applyCustomValues(T instance) {
        Map<String, Field> fieldMap = new HashMap<>();
        getAllFields(targetClass).forEach(field -> fieldMap.put(field.getName(), field));

        for (Map.Entry<String, Object> entry : customValues.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            Field field = fieldMap.get(fieldName);

            if (field == null) {
                throw new FieldAccessException(fieldName, targetClass, "Field not found in class hierarchy");
            }

            try {
                field.setAccessible(true);
                field.set(instance, value);
                log.debug("Set custom value for field '{}': {}", fieldName, value);
            } catch (IllegalAccessException e) {
                String message = String.format("Failed to set value for field %s", fieldName);
                log.error(message, e);
                throw new RuntimeException(message, e);
            }
        }
    }

    private void setDefaultValues(T instance) {
        List<Field> allFields = getAllFields(targetClass);
        log.debug("EntityBuilder[id={}] setting default values for {} fields in {}", builderId, allFields.size(), targetClass.getName());

        for (Field field : allFields) {
            try {
                field.setAccessible(true);

                if (!customValues.containsKey(field.getName())) {
                    Object defaultValue = getDefaultValue(field);
                    log.trace("EntityBuilder[id={}] setting default value for field '{}' to: {}", builderId, field.getName(), defaultValue);

                    if (defaultValue != null) {
                        field.set(instance, defaultValue);
                    } else {
                        log.warn("EntityBuilder[id={}] no default value defined for field '{}' of type {}", builderId, field.getName(), field.getType().getName());
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("EntityBuilder[id={}] failed to set default value for field '{}': {}", builderId, field.getName(), e.getMessage(), e);
                throw new RuntimeException("Failed to set default value for field " + field.getName(), e);
            }
        }
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;

        while (currentClass != null && !currentClass.equals(Object.class)) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        return fields;
    }

    private Object getDefaultValue(Field field) {
        Class<?> type = field.getType();
        String fieldName = field.getName();

        if (type == byte.class || type == Byte.class) return (byte) 1;
        if (type == short.class || type == Short.class) return (short) 1;
        if (type == int.class || type == Integer.class) return 1;
        if (type == long.class || type == Long.class) return 1L;
        if (type == float.class || type == Float.class) return 1.0f;
        if (type == double.class || type == Double.class) return 1.0d;

        if (type == char.class || type == Character.class) return 'A';
        if (type == boolean.class || type == Boolean.class) return false;

        if (type == String.class) return "test_" + fieldName;

        if (type == LocalDate.class) return LocalDate.now();
        if (type == LocalDateTime.class) return LocalDateTime.now();

        if (type == List.class) return new ArrayList<>();
        if (type == Set.class) return new HashSet<>();
        if (type == Map.class) return new HashMap<>();

        if (type == BigDecimal.class) return BigDecimal.ONE;

        if (type.isEnum()) {
            Object[] enumConstants = type.getEnumConstants();
            return enumConstants.length > 0 ? enumConstants[0] : null;
        }

        log.debug("Using default value null for type: {} (field: {})", type.getName(), fieldName);
        return null;
    }

    private boolean isAssignable(Class<?> fieldType, Class<?> valueType) {
        if (fieldType.isAssignableFrom(valueType)) {
            return true;
        }

        if (fieldType == byte.class && valueType == Byte.class) {
            return true;
        }
        if (fieldType == short.class && valueType == Short.class) {
            return true;
        }
        if (fieldType == int.class && valueType == Integer.class) {
            return true;
        }
        if (fieldType == long.class && valueType == Long.class) {
            return true;
        }
        if (fieldType == float.class && valueType == Float.class) {
            return true;
        }
        if (fieldType == double.class && valueType == Double.class) {
            return true;
        }
        if (fieldType == boolean.class && valueType == Boolean.class) {
            return true;
        }
        if (fieldType == char.class && valueType == Character.class) {
            return true;
        }

        if (isNumericAssignable(fieldType, valueType)) {
            return true;
        }

        return false;
    }

    private void validateFieldValue(Field field, Object value) {
        if (value != null) {
            Class<?> fieldType = field.getType();
            Class<?> valueType = value.getClass();

            if (!isAssignable(fieldType, valueType)) {
                log.error("EntityBuilder[id={}] invalid value type for field '{}': expected {} but got {}", builderId, field.getName(), fieldType.getName(), valueType.getName());
                throw new FieldAccessException(field.getName(), targetClass,
                        String.format("Value of type '%s' cannot be assigned to field of type '%s'", valueType.getName(), fieldType.getName()));
            }
        }
    }

    private boolean isNumericAssignable(Class<?> fieldType, Class<?> valueType) {
        Class<?>[] numericTypes = {
                byte.class, Byte.class,
                short.class, Short.class,
                int.class, Integer.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class
        };

        int fieldIndex = -1;
        int valueIndex = -1;
        for (int i = 0; i < numericTypes.length; i++) {
            if (numericTypes[i] == fieldType) fieldIndex = i;
            if (numericTypes[i] == valueType) valueIndex = i;
        }

        if (fieldIndex != -1 && valueIndex != -1) {
            return valueIndex <= fieldIndex;
        }

        return false;
    }
}
