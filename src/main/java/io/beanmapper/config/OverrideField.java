package io.beanmapper.config;

import java.util.function.Supplier;

import io.beanmapper.utils.BeanMapperLogger;

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

    public void block() {
        this.block = true;
    }

    public T get() {
        if (this.block) {
            return null;
        }
        if (this.value == null) {
            this.value = BeanMapperLogger.logTimed("Retrieving nested configuration field.", this.supplier);
        }
        return value;
    }

}
