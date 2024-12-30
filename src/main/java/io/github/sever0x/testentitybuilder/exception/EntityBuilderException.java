package io.github.sever0x.testentitybuilder.exception;

/**
 * Base exception class for all exceptions thrown by the entity builder framework.
 * Provides common functionality for handling and reporting builder-related errors.
 */
public class EntityBuilderException extends RuntimeException {
    /**
     * Creates a new exception with the specified error message.
     *
     * @param message The detailed error message
     */
    public EntityBuilderException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the specified error message and cause.
     *
     * @param message The detailed error message
     * @param cause The underlying cause of this exception
     */
    public EntityBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
