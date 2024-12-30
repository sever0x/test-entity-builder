package io.github.sever0x.testentitybuilder.exception;

/**
 * Exception thrown when the builder fails to create a new instance of the target entity.
 * This typically occurs when there are problems with the entity's constructor or instantiation process.
 */
public class ObjectCreationException extends EntityBuilderException {
    private final Class<?> targetClass;

    /**
     * Creates a new object creation exception with information about the failed instantiation.
     *
     * @param targetClass The class that failed to instantiate
     * @param message Details about why the instantiation failed
     */
    public ObjectCreationException(Class<?> targetClass, String message) {
        super(String.format("Failed to create instance of '%s': %s", targetClass.getName(), message));
        this.targetClass = targetClass;
    }
}
