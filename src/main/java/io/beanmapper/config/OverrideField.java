package io.beanmapper.config;

import java.util.function.Supplier;

import io.beanmapper.utils.BeanMapperPerformanceLogger;

@Deprecated(forRemoval = true, since = "4.1.4")
public class OverrideField<T> {

    private static final String LOGGING_STRING = "OverrideField#get(void) -> OverrideField#get(void)";
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
            this.value = BeanMapperPerformanceLogger.runTimed(LOGGING_STRING, this.supplier);
        }
        return value;
    }
}
