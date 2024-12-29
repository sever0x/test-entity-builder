package io.github.sever0x.exception;

public class EntityBuilderException extends RuntimeException {
    public EntityBuilderException(String message) {
        super(message);
    }

    public EntityBuilderException(String message, Throwable cause) {
        super(message, cause);
    }
}
