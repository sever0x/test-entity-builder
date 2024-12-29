package io.github.sever0x;

import io.github.sever0x.exception.FieldAccessException;
import io.github.sever0x.exception.ObjectCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class EntityBuilder<T> {
    private static final Logger log = LoggerFactory.getLogger(EntityBuilder.class);

    private final Class<T> targetClass;
    private final Map<String, Object> customValues = new HashMap<>();

    private EntityBuilder(Class<T> targetClass) {
        this.targetClass = targetClass;
        log.debug("Creating EntityBuilder for class: {}", targetClass.getName());
    }

    public static <T> EntityBuilder<T> of(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Target class can't be null");
        }
        return new EntityBuilder<>(clazz);
    }

    public EntityBuilder<T> with(String fieldName, Object value) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            validateFieldValue(field, value);
            customValues.put(fieldName, value);
            return this;
        } catch (NoSuchFieldException e) {
            throw new FieldAccessException(fieldName, targetClass, "Field doesn't exist");
        }
    }

    public T build() {
        try {
            log.info("Building instance of class: {}", targetClass.getName());

            Constructor<T> constructor = targetClass.getDeclaredConstructor();
            if (!Modifier.isPublic(constructor.getModifiers())) {
                throw new ObjectCreationException(targetClass, "No public no-args constructor available");
            }

            T instance = targetClass.getDeclaredConstructor().newInstance();
            setDefaultValues(instance);
            applyCustomValues(instance);

            log.debug("Successfully built instance of class: {}", targetClass.getName());
            return instance;
        } catch (NoSuchMethodException e) {
            throw new ObjectCreationException(targetClass, "Class must have a no-args constructor");
        } catch (SecurityException e) {
            throw new ObjectCreationException(targetClass, "Security manager prevents access to constructor");
        } catch (Exception e) {
            throw new ObjectCreationException(targetClass, "Unexpected error: " + e.getMessage());
        }
    }

    private void applyCustomValues(T instance) {
        customValues.forEach((fieldName, value) -> {
            try {
                Field field = targetClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(instance, value);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set value for field " + fieldName, e);
            }
        });
    }

    private void setDefaultValues(T instance) {
        for (Field field : targetClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                if (!customValues.containsKey(field.getName())) {
                    Class<?> fieldType = field.getType();
                    if (fieldType.isPrimitive()) {
                        Object defaultValue = getDefaultValue(field);
                        field.set(instance, defaultValue);
                        log.debug("Set default value for primitive field '{}': {}", field.getName(), defaultValue);
                    } else if (field.get(instance) == null) {
                        Object defaultValue = getDefaultValue(field);
                        field.set(instance, defaultValue);
                        log.debug("Set default value for object field '{}': {}", field.getName(), defaultValue);
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("Failed to set default value for field '{}' in class '{}'", field.getName(), targetClass.getName(), e);
            }
        }
    }

    // todo: maybe refactor this?
    private Object getDefaultValue(Field field) {
        Class<?> type = field.getType();
        String fieldName = field.getName();
        if (type == String.class) {
            return "test_" + fieldName;
        }
        if (type == int.class || type == Integer.class) {
            return 1;
        }
        if (type == boolean.class || type == Boolean.class) {
            return false;
        }
        return null;
    }

    private boolean isAssignable(Class<?> fieldType, Class<?> valueType) {
        if (fieldType.isAssignableFrom(valueType)) {
            return true;
        }
        if (fieldType == int.class && valueType == Integer.class) {
            return true;
        }
        if (fieldType == long.class && valueType == Long.class) {
            return true;
        }
        if (fieldType == boolean.class && valueType == Boolean.class) {
            return true;
        }
        // todo: add other primitives
        return false;
    }

    private void validateFieldValue(Field field, Object value) {
        if (value != null) {
            Class<?> fieldType = field.getType();
            Class<?> valueType = value.getClass();

            if (!isAssignable(fieldType, valueType)) {
                throw new FieldAccessException(field.getName(), targetClass,
                        String.format("Value of type '%s' cannot be assigned to field of type '%s'",
                                valueType.getName(), fieldType.getName()));
            }
        }
    }
}
