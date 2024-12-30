package io.github.sever0x.testentitybuilder.exception;

/**
 * Exception thrown when there are problems accessing or setting field values during entity building.
 * This can occur due to missing fields, incompatible types, or security restrictions.
 */
public class FieldAccessException extends EntityBuilderException {
    private final String fieldName;
    private final Class<?> targetClass;

    /**
     * Creates a new field access exception with detailed information about the problematic field.
     *
     * @param fieldName The name of the field that caused the exception
     * @param targetClass The class containing the field
     * @param message Additional details about the error
     */
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
