package io.github.sever0x.testentitybuilder.core.builder;

import io.github.sever0x.testentitybuilder.core.EntityBuilder;

/**
 * Base class for creating custom entity builders.
 * Provides common functionality for constructing entities and setting properties.
 *
 * @param <T> The type of the entity to be built.
 * @param <B> The specific builder type extending this class.
 */
public abstract class AbstractEntityBuilder<T, B extends AbstractEntityBuilder<T, B>> {
    protected final EntityBuilder<T> delegate;
    protected final Class<T> targetClass;

    /**
     * Initializes a new instance of the builder for the specified entity type.
     *
     * @param targetClass The class of the entity to build.
     */
    public AbstractEntityBuilder(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.delegate = EntityBuilder.of(targetClass);
    }

    /**
     * Builds an instance of the entity.
     *
     * @return A fully constructed entity of type {@code T}.
     */
    public T build() {
        return delegate.build();
    }

    /**
     * Provides a self-reference for method chaining in derived builder classes.
     *
     * @return The current instance of the builder.
     */
    @SuppressWarnings("unchecked")
    protected B self() {
        return (B) this;
    }
}
