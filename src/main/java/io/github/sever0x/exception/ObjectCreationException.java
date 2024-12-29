package io.github.sever0x.exception;

public class ObjectCreationException extends EntityBuilderException {
    private final Class<?> targetClass;

    public ObjectCreationException(Class<?> targetClass, String message) {
        super(String.format("Failed to create instance of '%s': %s", targetClass.getName(), message));
        this.targetClass = targetClass;
    }
}
