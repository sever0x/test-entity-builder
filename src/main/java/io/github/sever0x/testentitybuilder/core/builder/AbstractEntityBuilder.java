package io.github.sever0x.testentitybuilder.core.builder;

import io.github.sever0x.testentitybuilder.core.EntityBuilder;

public abstract class AbstractEntityBuilder<T, B extends AbstractEntityBuilder<T, B>> {
    protected final EntityBuilder<T> delegate;
    protected final Class<T> targetClass;

    public AbstractEntityBuilder(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.delegate = EntityBuilder.of(targetClass);
    }

    public T build() {
        return delegate.build();
    }

    @SuppressWarnings("unchecked")
    protected B self() {
        return (B) this;
    }
}
