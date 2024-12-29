package io.github.sever0x.exception;

public class FieldAccessException extends EntityBuilderException {
    private final String fieldName;
    private final Class<?> targetClass;

    public FieldAccessException(String fieldName, Class<?> targetClass, String message) {
        super(String.format("Failed to access field '%s' in class '%s': %s", fieldName, targetClass.getName(), message));
        this.fieldName = fieldName;
        this.targetClass = targetClass;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
