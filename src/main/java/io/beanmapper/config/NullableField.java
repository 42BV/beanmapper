package io.beanmapper.config;

public class NullableField<T> {

    private T payload;

    public NullableField(T payload) {
        this.payload = payload;
    }

    public T get() {
        return payload;
    }

}
