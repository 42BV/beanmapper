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
        if (value == null) {
            this.block = true;
        } else {
            this.value = value;
            this.block = false;
        }
    }

    public void reset() {
        this.block = true;
    }

    public T get() {
        if (this.block) {
            return null;
        }
        return this.value == null ?
                this.supplier.get() :
                this.value;
    }

}