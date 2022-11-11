package io.beanmapper.config;

import java.util.function.Supplier;

public class OverrideField<T> {

    private final Supplier<T> supplier;

    private boolean block = false;

    private T value = null;

    public OverrideField(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public void set(T value) {
        this.block |= value == null;
        this.value = value;
    }

    public void block() {
        this.block = true;
    }

    public T get() {
        if (this.block) {
            return value;
        }
        return this.value == null ?
                this.supplier.get() :
                this.value;
    }

}